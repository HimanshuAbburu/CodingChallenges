from typing import Tuple

def frequency_counter(text: str) -> dict:
    frequencies = {}

    for char in text:
        if char not in frequencies:
            frequencies[char] = 1
            continue
        frequencies[char] += 1
    
    return sorted(frequencies.items(), key=lambda x: x[1], reverse=True)

# Node class for huffman tree

class NodeTree:
    def __init__(self, left = None, right = None, data = None) -> None:
        self.left = left
        self.right = right
        self.data = data

    def children(self):
        return (self.left, self.right)
    
    def __str__(self): 
        return f"0:{str(self.left)} 1:{str(self.right)}"


def huffman_tree_to_dict(node, bin_string: str='') -> dict:
    if type(node)  is str:
        return {node: bin_string}
    
    (l,r) = node.children()
    tree = dict()
    tree.update(huffman_tree_to_dict(l, bin_string + '0'))
    tree.update(huffman_tree_to_dict(r, bin_string + '1'))

    return tree

def create_huffman_tree(nodes: list):
    while len(nodes) > 1:
        first = nodes[-1]
        second = nodes[-2]
        nodes = nodes[:-2]
        node = NodeTree(first[0], second[0])
        nodes.append((node, first[1] + second[1]))
        nodes = sorted(nodes, key=lambda x: x[1], reverse=True)

    return nodes[0][0]

def huffman_tree_from_dict(huffman: dict) -> NodeTree:
    tree = NodeTree()

    for char, bin_string in huffman.items():
        node = tree
        for bit in bin_string:
            if bit == '0':
                if not node.left:
                    node.left = NodeTree()
                node = node.left
            elif bit == '1':
                if not node.right:
                    node.right = NodeTree()
                node = node.right
        node.data = char
    
    return tree

def compress(text: str) -> Tuple[bytes, dict, int]:
    frequencies = frequency_counter(text)
    tree = create_huffman_tree(frequencies)
    huffman = huffman_tree_to_dict(tree)
    compressed = ''.join([huffman[char] for char in text])

    msg_len = len(compressed)
    compressed = int(compressed,2).to_bytes((len(compressed) + 7) // 8, byteorder='big')
    return compressed, huffman, msg_len

def decompress(compressed: SyntaxError, huffman: dict, msg_len: int) -> str:
    decompressed = ''
    if compressed != msg_len:
        compressed = (msg_len - len(compressed)) * '0' + compressed
    root = huffman_tree_from_dict(huffman)
    current = root
    for i in range(msg_len):
        if compressed[i] == '0':
            current = current.left
        elif compressed[i] == '1':
            current = current.right

        if current.left is None and current.right is None:
            decompressed += current.data
            current = root
    
    return decompressed