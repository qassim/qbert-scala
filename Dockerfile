FROM openjdk:jdk

ENV SCALA_VERSION 2.11.11
ENV SBT_VERSION 0.13.15
ENV SBT_OPTS -Xmx2G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xss2M -Duser.timezone=GMT

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

WORKDIR /qbert

ADD . /qbert

CMD sbt run



