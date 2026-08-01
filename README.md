# Hurricane

Hurricane removes the bamboo and pointed-dripstone rubberbanding that Bedrock
players hit through Geyser, without changing collision for Java players or mobs.

Java and Bedrock apply a different position-based random offset to bamboo and
pointed dripstone, so the stalk the Bedrock client renders never lines up with
the one the Java server collides against. Walking past it then triggers movement
correction. Hurricane gives each Bedrock player the collision shape their own
client renders, computed from Bedrock's offset.

## Supported platforms

- Paper 1.20.5 through 26.2
- Folia (tested on 26.1.2)
- Fabric 26.2 (requires Fabric API)
- NeoForge 26.2

## Dependencies

- Geyser or Floodgate on the server, to detect Bedrock players
- ViaVersion on Paper servers older than 26.2

## Installing

Drop the jar for your platform into `plugins/` (Paper) or `mods/`
(Fabric, NeoForge). Both fixes are enabled by default.

Both fixes change how the server validates Bedrock movement near bamboo and
pointed dripstone, so test a new build before running it in production.
