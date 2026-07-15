# Client sends an array of strings.
# Arrays contents aret he command and its arguments that the server should execute.


# command = ["SET", "mykey", "myvalue"]
# command = ["SET", "mykey", 10]

command = [ "SET", 123,["GET", "key"]]


DEFAULT_SEPARATION = '\\r\\n'

def serialise(command):
    if command is None:
        return "$-1\r\n"
    elif isinstance(command, str):
        return "$" + serialiseString(command)
    elif isinstance(command, list):
        return "*" + serialiseLists(command)
    elif isinstance(command, int):
        return ":" + serialiseNumbers(command)
    # else:
    #     return "-1" + serialise(command)

def serialiseLists(list):
    result =  str(len(list)) + DEFAULT_SEPARATION
    for item in list:
        result += serialise(item)
    return result

def serialiseString(command):
    return str(len(command)) + DEFAULT_SEPARATION + command + DEFAULT_SEPARATION

def serialiseNumbers(command):
     return str(command) + DEFAULT_SEPARATION

def serialiseNone(command):
    return DEFAULT_SEPARATION

# print(serialise([None, None]))
# print(serialise(command))
# print(serialise("hello")) # $5\r\nhello\r\n

# print(serialise("")) # $0\r\n\r\n

# print(serialise(42))
# print(serialise(None))
# print(serialise([]))