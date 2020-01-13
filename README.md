# CityZen
The premier City-management Bukkit plugin

Player-run cities have become a prominent feature on the Core Server, but running and maintaining them can be a hassle. CityZen is a premier city-management plugin that aims to make the life of the Mayor easier.

## Summary:

In CityZen, a player can create a city wherever they want. The space where the city is founded will become the first of a series of protected plots that are only accessible by members of its city. As players join the city, each one gets their own plot for building, thus growing the city itself. The Mayor of the city has full control over who can join the city, as well as evicting inactive or unruly citizens. Furthermore, citizens can increase the reputation of their city by completing server tasks, such as voting for the server, winning arena matches, and completing quests. The better your city's rep, the better rewards everyone gets.

## Reporting Issues:
Please [see this page](https://github.com/griffenx/CityZen/wiki/Reporting-Issues) on how to properly report issues. Help us help you help us help you!

## Details:

Visit [the wiki](https://github.com/griffenx/CityZen/wiki) to learn in-depth how to use this plugin.

In order to fully grasp how CityZen works, there are a few basic concepts to grasp.

**Citizen** - A citizen is any player on the server. Typically this refers to a player who is a member of a city, however nomadic players are technically just city-less citizens. A citizen has an individual reputation, and can be affiliated with one city at a time.

**City** - A city is a collection of protected plots belonging to players. Each city has a Mayor, and may have any number of Deputies. The reputation of a city is the sum of the reputations of all its players. By default, a city may only be joined with permission of its Mayor or a Deputy, however the Mayor can choose to bypass this by allowing any player to join at will.

**Plot** - A plot is an area of land belonging to a citizen of a city. This land is protected such that only the owner(s) may place and break blocks there. When a player leaves a city or is evicted, their plot is cleared.

**Mayor** - A Mayor is the founder of a city or a player who has inherited the title from the former Mayor. The Mayor is responsible for administration of their city, including managing citizens, controlling city properties, and more. A Mayor may appoint Deputies to aid them in their duties. A city may have only one Mayor.

**Deputy** - A Deputy is a player appointed by the Mayor with limited mayoral powers. Deputies have permission to invite and evict citizens, place new plots, but do not have control over basic city properties. A City may have an arbitrary number of Deputies.

## Installation:
Download the latest release or compile for yourself from the latest commit, then simply load up on your Bukkit/Spigot server like you would any other plugin. You'll probably want to customize your `config.yml` and `rewards.yml` files to your liking, as the defaults may not be what you want. Make sure to visit the [wiki](https://github.com/griffenx/CityZen/wiki) for more info.

## Credits:
This project is created and managed by Sarah Hawthorne ([iamvishnu](https://iamvishnu.net)/griffenx). Built with suggestions and testing by [The Minecraft Blog](http://the-minecraft-blog.com) community.
