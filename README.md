# #-ludus <img src="https://lh3.googleusercontent.com/vgUxZVRHQU_eDCJrKX1dH4RIlFgDs1hK3zo3EjnASGC9AWigw1Prr6dx0p3rjcM0WQ=w300-rw" width="120" align="right">

The simple and fun number combination game **#-ludus** goes 2.0 and open source.

<a href="https://play.google.com/store/apps/details?id=com.fallenritemonk.numbers">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

## What is #-ludus

\#-ludus ia a android game with the goal to eliminate all the numbers on the game field by combining those numbers that are same or sum up to 10.<br>
The player starts of with three rows, filled with the numbers from 1 to 19, without the number 10, as discrete digits.
He then has to eliminate all numbers by combining those above or next to each other (over empty fields and over new rows are also allowed).
If the point of no further combinations is reached, the remaining numbers get added to the end of the game field again, without the empty spaces.
This game flow continues until the player eliminates all the numbers on the game field.

## How to contribute

Everybody is welcomed and pleased to contribute to this project.<br>
If you are interested, there are several ways you can contribute:
* Distribute #-ludus to the world by telling your friends, posting on your social networks or writing in your blog about it
* Joining the [google group](https://groups.google.com/forum/#!forum/FRM-ludus/new) to get beta access, [download](https://play.google.com/store/apps/details?id=com.fallenritemonk.numbers) the app, test it and report bugs or submit enhancement ideas to the [issue tracker](https://github.com/FallenRiteMonk/ludus/issues)
* [Fork](https://guides.github.com/activities/forking/) the repository, contribute to the code by fixing bugs, implementing features or translating the app

No matter in which way you contribute to this project, the community and users are thankful.

### Translating

The project contains two places where it contains locale files, one in the source code under app/main/res/values, which are for the app it self, and one under resources/strings, which are for external strings like the play store.
To translate the locale files in the source code just follow the [android way](http://developer.android.com/training/basics/supporting-devices/language.html#CreateDirs) of working with locale string files.
The files in the resources folder follow a similar structure, just create a new file <languagecode>.xml, copy the en.xml content into it and start translating the texts between the xml tags (!!!DON'T CHANGE THE TAGS!!! ONLY THE TEXT!!!)

### Useful links

* [Branching model](http://nvie.com/posts/a-successful-git-branching-model/) describing the desired work flow.
* [Github contribution](https://guides.github.com/activities/contributing-to-open-source/#contributing) guidelines for contributing to an open source project.
* [Github fork](https://guides.github.com/activities/forking/) guidelines for forking to an open source project.
* [Android way](http://developer.android.com/training/basics/supporting-devices/language.html#CreateDirs) of handling locale files.

## Questions

Feel free to ask any question that comes to your mind in our [google group](https://groups.google.com/forum/#!forum/FRM-ludus/new),
not because it would bother me if you write me an e-mail, but because I there is no such thing as a dumb, stupid or useless question and therefore it might also be of value for somebody else.

Thanks
