# watchmedo shell-command --patterns='*.py' --recursive --command='python3 src/compressor_app/main.py' src/compressor_app

from pathlib import Path

file = Path(__file__).parent / "resources" / "test.txt"

CHARACTER_FREQUENCY = {}

try:
    with open(file, "r") as file:
        text = file.read()
        for char in text:
            CHARACTER_FREQUENCY[char] = CHARACTER_FREQUENCY.get(char, 0) + 1
        
    # print(CHARACTER_FREQUENCY['X'])
    

except FileNotFoundError:
    print(f"File not found : {file}")
except PermissionError:
    print(f"Permission denied : {file}")
except Exception as e:
    print(f"An error occurred : {e}")

# file = open("./resources/test.txt", "r")
# print(file.read())