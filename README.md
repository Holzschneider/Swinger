# Swinger
An incomplete and inconsistent collection of fancy custom javax.swing-components only mac users ever missed.



Release
-------

The base release 1.0.0 corresponds to the unmodified implementation, as it's extracted from several legacy projects, where it has originally evolved from.

Releases are deployed automatically to the deploy branch of this github repostory. 
To add a dependency to *Swinger* using maven, modify your *repositories* section to include the git based repository.

	<repositories>
	 ...
	  <repository>
	    <id>Swinger-Repository</id>
	    <name>Swinger's Git-based repo</name>
	    <url>https://raw.githubusercontent.com/Holzschneider/Swinger/deploy/</url>
	  </repository>
	...
	</repositories>
	
and modify your *dependencies* section to include the dependency
 
	  <dependencies>
	  ...
	  	<dependency>
	  		<groupId>de.dualuse.commons</groupId>
	  		<artifactId>Swinger</artifactId>
	  		<version>1.0.0</version>
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
	  compile 'de.dualuse.commons:Swinger:1.0.+'
	}

