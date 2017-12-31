# qbert

Another slack bot, written in scala. Slack API via [slack-scala-client](https://github.com/gilbertw1/slack-scala-client). 

## Work in progress

This is still in the early stages of the project, the motivation for this project is to learn more about scala so progress won't be as quick as it would otherwise be and code quality probably won't be great. For now, I'm in the 'rush towards functionality' mode, meaning I get things working regardless of whether or not the approach is a good one.

This will be part of the reason for slow progress, as I'll go back and iterate on functionality as I learn more about how to write good scala code.

## Getting Started

_As this is very, very unfinished, there won't be much point in using this yet, but if you do want to.._

It's a standard SBT project, so build and run with SBT. In the `src/main/resources` directory, create a new configuration file `api.conf` and populate the file with the following for basic slack compatibility:

`slack.apikey="API_TOKEN_HERE"`

There's a basic (and very flawed) plugin to get [Manchester Metrolink](https://en.wikipedia.org/wiki/Manchester_Metrolink) information, provided you have an API key from the Transport for Greater Manchester's [developer portal](https://developer.tfgm.com/). Once you have this key, add another entry in `api.conf` as so:

`plugin.metrolink.apikey="API_KEY_HERE"`

If you want to write your own plugins, I'm not going to provide any documentation yet as this is far from final, but feel free to have a look at what is currently there to try and figure it out.