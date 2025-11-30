import argparse
from compression import compress, decompress

parser = argparse.ArgumentParser(description='Compress or decompress a file using Huffman coding.', prog='main.py', usage='%(prog)s [options] file')

parser.add_argument('file', type=str, help='The file to compress or decompress.')
parser.add_argument('-c', '--compress', action='store_true', help='Compress a text file')
parser.add_argument('-d', '--decompress', action='store_true', help='Decompress a text file')
parser.add_argument('-o', '--output', type=str, help='Output file name')

args = parser.parse_args()

input_file = args.file

if args.output:
    output_file = args.output
else:
    output_file = input_file
    try:
        output_file = input_file.split('.')[0]
    except IndexError:
        pass

    if args.compress:
        output_file += '.compressed'
    if args.decompress:
        output_file += '.decompressed'



if args.compress:
    with open(input_file, 'r') as file:
        text = file.read()
        compressed, huffman, msg_len = compress(text)
        with open(output_file, 'wb') as file:
            file.write(str(huffman).encode())   # write huffman table
            file.write(b'\n')
            file.write(str(msg_len).encode())   # write msg_len
            file.write(b'\n')
            file.write(compressed)              # write binary data

       
if args.decompress:
    with open(input_file, 'rb') as file:
        huffman = eval(file.readline().decode())
        msg_len = int(file.readline().decode())
        compressed = file.read()
        compressed = str(bin(int(compressed.hex(), 16))).replace('0b', '')
        decompressed = decompress(compressed, huffman, msg_len)
        with open(output_file, 'w') as file:
            file.write(decompressed)