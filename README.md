## Codename One Meme Maker Demo

This is a simple app that allows users to design memes to be shared on their social media feeds.  Users can select an image to serve as a backdrop, and edit text to be overlaid on the top and bottom of the image.  When complete, the image can be exported to the photo library, or shared directly to their social media feed (e.g. Facebook or Twitter).

This app is available in the Google Play, iTunes, and Microsoft stores.

### Screen Shots

![Android screen 1](https://raw.githubusercontent.com/wiki/shannah/mememaker/img/mememaker-android1.png)

![Android screen 1](https://raw.githubusercontent.com/wiki/shannah/mememaker/img/mememaker-android2.png)

### Notable Features

* **File Type Associations** - This app is registered to be able to open image files on Android, iOS, and Windows 10.  This is accomplished using build hints.  For a full description of the process, please see the blog post on the topic.  (Link to be posted).
* **Cool Font Effects** - This app uses the [CN1FontBox](https://github.com/shannah/CN1FontBox) cn1lib to provide a Meme font that can be outlined in black, and stretched horizontally.  CN1FontBox provides access to the actual glyph shapes so that we can perform arbitrary transformations on it, and produce nice effects.
* **CSS** - Portions of this app are styled using CSS (via the [cn1-css](https://github.com/shannah/cn1-css) plugin.  CSS is used to import images as multi-images so that they are automatically resized to the correct resolution for the runtime device density.  See the blob post on the topic for more information.  (Link to be posted).

### Get the Meme Maker App

* [On the Play Store](https://play.google.com/store/apps/details?id=com.codename1.demos.mememaker)
* [In the Windows Store](https://www.microsoft.com/en-us/store/p/codename-one-meme-maker/9nblggh441nf)
* [In the iTunes Store](https://itunes.apple.com/us/app/codename-one-meme-maker/id1171538632)

