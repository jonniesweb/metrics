# Metrics Examples
Examples of collecting various metrics and exporting them to StatsD. Metrics can then be graphed using Graphite.


## Files

Contains exapmples of various ways to gather metrics. Each metric generates random data, which is then exported to StatsD.

## Running

`mvn package -DskipTests=true` then `java -cp target/java-graphite-0.0.1-SNAPSHOT-jar-with-dependencies.jar ca.jonsimpson.metrics.Main`. This will run a script that sends demo data to a StatsD instance at localhost:8125.

To capture and visualize this data, launch [this docker image of Graphite + Grafana + StatsD](https://registry.hub.docker.com/u/kamon/grafana_graphite/) using the following command: `docker run -d -p 80:80 -p 8125:8125/udp -p 8126:8126 --name kamon-grafana-dashboard kamon/grafana_graphite`
