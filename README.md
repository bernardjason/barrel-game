# barrel-game

Not a great project, decided on box2d during development when needs to be from the start 
then render textures etc at display time. Then decided on browser version, then gestures
and finally render on phone browser dimensions as well as desktop.


to compile and run on desktop
```commandline
./gradlew desktop:run
```

to run browser version
```commandline
./gradlew html:superDev
```

then open
http://127.0.0.1:8080/index.html

or to build optimised html
```commandline
./gradlew html:dist
```
