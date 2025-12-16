<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="blocks" tilewidth="256" tileheight="170" tilecount="3" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="3" type="BlockOrange">
  <properties>
   <property name="color" value="orange"/>
   <property name="destroyable" type="bool" value="true"/>
   <property name="displayHeight" type="float" value="100"/>
   <property name="displayWidth" type="float" value="160"/>
   <property name="hp" type="int" value="1"/>
   <property name="scoreValue" type="int" value="10"/>
  </properties>
  <image source="../tilesets/blocks/block_orange.png" width="256" height="170"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="57.8309" y="42.0835" width="140.038" height="85.786"/>
  </objectgroup>
 </tile>
 <tile id="8" type="BlockGreen">
  <properties>
   <property name="color" value="green"/>
   <property name="destroyable" type="bool" value="true"/>
   <property name="displayHeight" type="float" value="100"/>
   <property name="displayWidth" type="float" value="160"/>
   <property name="hp" type="int" value="1"/>
   <property name="scoreValue" type="int" value="10"/>
  </properties>
  <image source="../tilesets/blocks/block_green.png" width="256" height="170"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="55.8779" y="44.0491" width="143.985" height="81.9697"/>
  </objectgroup>
 </tile>
 <tile id="9" type="BlockRed">
  <properties>
   <property name="color" value="red"/>
   <property name="destroyable" type="bool" value="true"/>
   <property name="displayHeight" type="float" value="100"/>
   <property name="displayWidth" type="float" value="160"/>
   <property name="hp" type="int" value="1"/>
   <property name="scoreValue" type="int" value="10"/>
  </properties>
  <image source="../tilesets/blocks/block_red.png" width="256" height="170"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="56.9058" y="40.2119" width="143.042" height="88.6773"/>
  </objectgroup>
 </tile>
</tileset>
