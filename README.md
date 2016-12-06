# JDaeLoader - Java Dae Loader

This is a simple Collada file loader written in Java. Currently it only has
support for a small portion of [Collada version 1.4.1](https://www.khronos.org/files/collada_spec_1_4.pdf).

## Things Left to Implement

- Connect bones to respective mesh.
- Connect animation key frames to respective bones.
- Create abstract classes so user can implement render functions.

## Usage

Loading a model...

``` java
DaeLoader loader = new DaeLoader();
loader.loadModel("./models/Goofy.dae");
```
