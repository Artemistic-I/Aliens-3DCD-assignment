
IMPORTANT! Please wait when you run the program. It should work after a few seconds. I believe the white screen at the beginning is the result of using large number of high resolution textures.
It takes a bit of time to load.

I slightly modified gmaths package. I added an extra method to Mat4.java to multiply a Mat4 by Vec4. I needed that to set the position of spotlightLight.

falling snow image by Dillon Kydd is free to use under unsplash licence https://unsplash.com/photos/a-black-and-white-photo-of-snow-falling-7o7m1xCEiY8
all other images are in public domain under C0 taken from
https://polyhaven.com/textures and https://polyhaven.com/a/snowy_forest_path_01

I reused the code from labs and some code from Joey de Vries's tutorial for implementing the spotlight behaviour in shaders.


List of new files excluding textures:

    5 new/modified shaders:
        fs_animated_2t.txt
        vs_animated.txt
        fs_standard_ms_0t.txt
        fs_standard_ms_1t.txt
        fs_standard_ms_2t.txt

    3 new java files:
        AlienModel.java
        Spotlight.java
        SpotlightLight.java

Other files are modified or unchanged.