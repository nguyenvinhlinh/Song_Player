Author: Nguyen Vinh Linh

Date: August 18, 2014


This is a media player which serve a main purpose to play media as a module of a
main project.

HOW TO RUN:
--network: `online` or `offline`
--type: `id` or `fullurl`
--source: the source of media

`java -jar Song_Player.jar --network=online --type=id source=jlUvzRBIjyE`
---> this command will automatically play a youtube song with `jlUvzRBIjyE` as
id.

`java -jar Song_Player.jar --network=online --type=fullurl
--source=https://www.youtube.com/embed?v=jlUvzRBIjyE`.
---> this command will play this youtube video or any online video.

`java -jar Song_Player.jar --network=offline --type=fullurl
--source=file:///home/Link/a.mp4`.
---> this command will lauch a player which play file a.mp3 locating in
/home/Link directory.

REFERENCES:
http://docs.oracle.com/javafx/2/media/playercontrol.htm
