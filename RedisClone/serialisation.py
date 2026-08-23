from abc import ABC, abstractmethod

CRLF = "\r\n"
NULL_LENGTH = -1


# --------------------------------------------------------------------------- #
# Reader: a single, reusable way to consume a RESP byte stream.
# --------------------------------------------------------------------------- #
class Reader:
    """A forward-only cursor over a RESP string."""

    def __init__(self, data):
        self._data = data
        self._pos = 0

    def read_char(self):
        if self._pos >= len(self._data):
            raise ValueError("Incomplete RESP message: expected a type prefix")
        char = self._data[self._pos]
        self._pos += 1
        return char

    def read_line(self):
        end = self._data.find(CRLF, self._pos)
        if end == -1:
            raise ValueError("Incomplete RESP message: missing separator")
        line = self._data[self._pos:end]
        self._pos = end + len(CRLF)
        return line

    def read_exact(self, length):
        end = self._pos + length
        if end + len(CRLF) > len(self._data):
            raise ValueError("Incomplete RESP message: bulk string too short")
        value = self._data[self._pos:end]
        self._pos = end + len(CRLF)
        return value

    @property
    def remaining(self):
        return self._data[self._pos:]


# --------------------------------------------------------------------------- #
# RespType: the abstraction every concrete type depends on.
# --------------------------------------------------------------------------- #
class RespType(ABC):
    prefix = ""

    @abstractmethod
    def can_serialise(self, value):
        """Return True if this handler owns `value` for encoding."""

    @abstractmethod
    def serialise(self, value, protocol):
        """Encode `value`'s body (the prefix is prepended by the protocol)."""

    @abstractmethod
    def deserialise(self, reader, protocol):
        """Decode this type's body from `reader` (prefix already consumed)."""


class BulkString(RespType):
    """`$` — length-prefixed string; also carries the RESP null (`$-1`)."""

    prefix = "$"

    def can_serialise(self, value):
        return value is None or isinstance(value, str)

    def serialise(self, value, protocol):
        if value is None:
            return f"{NULL_LENGTH}{CRLF}"
        return f"{len(value)}{CRLF}{value}{CRLF}"

    def deserialise(self, reader, protocol):
        length = int(reader.read_line())
        if length == NULL_LENGTH:
            return None
        return reader.read_exact(length)


class Integer(RespType):
    """`:` — a signed integer."""

    prefix = ":"

    def can_serialise(self, value):
        # bool is a subclass of int; RESP has no boolean, so exclude it.
        return isinstance(value, int) and not isinstance(value, bool)

    def serialise(self, value, protocol):
        return f"{value}{CRLF}"

    def deserialise(self, reader, protocol):
        return int(reader.read_line())


class Array(RespType):
    """`*` — an ordered collection; recurses through the protocol."""

    prefix = "*"

    def can_serialise(self, value):
        return isinstance(value, (list, tuple))

    def serialise(self, value, protocol):
        body = f"{len(value)}{CRLF}"
        for item in value:
            body += protocol.serialise(item)
        return body

    def deserialise(self, reader, protocol):
        count = int(reader.read_line())
        if count == NULL_LENGTH:
            return None
        return [protocol.read(reader) for _ in range(count)]


class SimpleString(RespType):
    """`+` — a short, unstructured status line (decode-only by default)."""

    prefix = "+"

    def can_serialise(self, value):
        return False

    def serialise(self, value, protocol):
        return f"{value}{CRLF}"

    def deserialise(self, reader, protocol):
        return reader.read_line()


class Error(RespType):
    """`-` — an error status line (decode-only by default)."""

    prefix = "-"

    def can_serialise(self, value):
        return False

    def serialise(self, value, protocol):
        return f"{value}{CRLF}"

    def deserialise(self, reader, protocol):
        return reader.read_line()


# --------------------------------------------------------------------------- #
# RespProtocol: dispatcher that owns the registry and the recursion.
# --------------------------------------------------------------------------- #
class RespProtocol:
    """Serialises/deserialises values using a registry of `RespType`s."""

    def __init__(self, types):
        self._types = list(types)
        self._by_prefix = {t.prefix: t for t in self._types}

    def register(self, resp_type):
        """Add a new type at runtime (Open/Closed in action)."""
        self._types.append(resp_type)
        self._by_prefix[resp_type.prefix] = resp_type
        return self

    def serialise(self, value):
        for resp_type in self._types:
            if resp_type.can_serialise(value):
                return resp_type.prefix + resp_type.serialise(value, self)
        raise TypeError(f"No RESP type can serialise: {value!r}")

    def deserialise(self, data):
        """Decode one message; return (value, unconsumed_remainder)."""
        if not data:
            raise ValueError("Cannot deserialise empty data")
        reader = Reader(data)
        value = self.read(reader)
        return value, reader.remaining

    def read(self, reader):
        """Decode one message from an existing cursor (used for recursion)."""
        prefix = reader.read_char()
        resp_type = self._by_prefix.get(prefix)
        if resp_type is None:
            raise ValueError(f"Unknown RESP prefix: {prefix!r}")
        return resp_type.deserialise(reader, self)


# --------------------------------------------------------------------------- #
# Default protocol instance + backward-compatible module-level facade.
# --------------------------------------------------------------------------- #
DEFAULT_PROTOCOL = RespProtocol([
    BulkString(),
    Integer(),
    Array(),
    SimpleString(),
    Error(),
])


def serialise(value):
    return DEFAULT_PROTOCOL.serialise(value)


def deserialise(data):
    return DEFAULT_PROTOCOL.deserialise(data)


if __name__ == "__main__":
    print(deserialise("$-1\r\n"))
    print(deserialise("*1\r\n$4\r\nping\r\n"))
    print(deserialise("*2\r\n$4\r\necho\r\n$11\r\nhello world\r\n"))
    print(deserialise("*2\r\n$3\r\nget\r\n$3\r\nkey\r\n"))
    print(deserialise("+OK\r\n"))
    print(deserialise("-Error message\r\n"))
    print(deserialise("$0\r\n\r\n"))
    print(deserialise("+hello world\r\n"))

    # Round-trip: serialise then deserialise returns the original value.
    print(deserialise(serialise(["SET", "mykey", 10])))
    print(deserialise(serialise(None)))
    print(deserialise(serialise(42)))


    print(serialise([None, None]))
    print(serialise([ "SET", 123,["GET", "key"]]))
    print(serialise("hello"))

    print(serialise(""))

    print(serialise(42))
    print(serialise(None))
    print(serialise([]))