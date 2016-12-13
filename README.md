# JDaeLoader - Java Dae Loader

This is a simple Collada file loader written in Java. Currently it only has support for a small portion of [Collada version 1.4.1](https://www.khronos.org/files/collada_spec_1_4.pdf).

This project does not implement the drawing of Collada Dae files. It leaves that up to the user. However, it makes the process very easy and will even generate the vbo and vba arrays for you. The details on how those arrays are generated will be documented at a later date.

## Things Left to Implement

- Read in texture urls.
- Apply pose matrices to the displayed meshes.
- Connect animation key frames to respective bones.
- Create abstract classes so user can implement render functions.

## Usage

Loading a model...

``` java
DaeLoader loader = new DaeLoader();
loader.loadModel("./models/Goofy.dae");
```
