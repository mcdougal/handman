wordlist = open("2of12inf.txt")
filtered = open("filtered.txt", 'w')

while 1:
    line = wordlist.readline()
    if not line:
        break
    if len(line) > 4 and len(line) < 10 and line.find("%") == -1:
		filtered.write(line)

wordlist.close()
filtered.close()