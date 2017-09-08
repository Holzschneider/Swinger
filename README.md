# Swinger
An incomplete and inconsistent collection of fancy custom javax.swing-components only mac users ever missed!



Release
-------

The base releases 1.x.y correspond to the unmodified implementation, as it's extracted from several legacy projects, where it has originally evolved from.

Releases are deployed automatically to the deploy branch of this github repostory. 
To add a dependency to *Swinger* using maven, modify your *repositories* section to include the git based repository.

	<repositories>
	 ...
	  <repository>
	    <id>dualuse repository</id>
	    <name>dualuse's git based repo</name>
	    <url>https://dualuse.github.io/maven/</url>
	  </repository>
	...
	</repositories>
	
and modify your *dependencies* section to include the dependency
 
	  <dependencies>
	  ...
	  	<dependency>
	  		<groupId>de.dualuse</groupId>
	  		<artifactId>Swinger</artifactId>
	  		<version>[1,)</version>
	  	</dependency>
	  ...
	  </dependencies>


To add the repository and the dependency using gradle refer to this

	repositories {
	    maven {
	        url "https://raw.githubusercontent.com/Holzschneider/Swinger/deploy/"
	    }
	}

and this

	dependencies {
	  compile 'de.dualuse:Swinger:1.+'
	}

