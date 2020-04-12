FROM williamyeh/java8

ENV SCALA_VERSION 2.12.2
ENV SBT_VERSION 1.0.4
ENV SBT_OPTS -Xmx512M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xss50M -Duser.timezone=GMT

# install scala
RUN \
  wget https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.deb && \
  dpkg -i scala-$SCALA_VERSION.deb && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# install sbt

RUN \
  wget https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm -f sbt-$SBT_VERSION.deb scala-$SCALA_VERSION.deb && \
  sbt sbtVersion

COPY . /qbert

WORKDIR /qbert

RUN sbt compile

CMD sbt run
