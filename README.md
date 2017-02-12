### ImageEncode

Converts strings to and from images.

#### Compiling

```
$ java -jar ImageEncode.jar
(C)ompile or (D)ecompile: C
To compile path: /home/user/Desktop/file_to_convert.txt
Path to save to: /home/user/Desktop/output.png
Translating string to an image at "/home/user/Desktop/file_to_convert.txt"

$
```

1) The program reads in the file passed in at the first prompt to a String. 
2) The string is converted three `char`s at a time to form a RGB pair.
3) A 400x400 image object is created with a randomly generated noise background.
4) The converted string is written to the image as a sequence of pixels at a random offset in the image.

---

#### Decompiling

<aside class="warning">
Decompiling large images takes a while, as every pixel represents three characters to be converted.
By default, this tool compiles to 400x400 images. That's 160,000 pixels or 480,000 characters to decompile.
I suggest you crop images you wish to decompile to a specific sequence of pixels for fast decompilation.
</aside>

```
$ java -jar ImageEncode.jar
(C)ompile or (D)ecompile: D
Image path: /home/user/Desktop/output.png
Path to save to: /home/user/Desktop/decompiled.txt
Decompiling image to file.
Wrote file /home/user/Desktop/decompiled.txt

$
```

1) The program converts the image passed at the first prompt to an `int[width * height]` array (pixel values).
2) The `int` array is iterated through, index by index, taking each color value as an `r`, `g`, and `b`. Each value represents one character, so they are appended to the converted string as `char`s.
3) The string is written to the file passed at the second prompt.


