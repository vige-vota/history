# Vige, Home of Professional Open Source Copyright 2010, Vige, and
# individual contributors by the @authors tag. See the copyright.txt in the
# distribution for a full listing of individual contributors.
# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM arm64v8/mongo:5.0
EXPOSE 8280
RUN apt-get -y update && \
	apt-get -y install sudo && \
	apt-get -y install software-properties-common && \
	apt-get -y install curl && \
	echo "%adm ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    useradd -u 1000 -G users,adm -d /home/votinguser --shell /bin/bash -m votinguser && \
    echo "votinguser:secret" | chpasswd && \
    apt-get -y update && \
    apt-get clean all && \
    apt-get -y autoremove && \
    chmod -R 777 /data

USER votinguser

ENV TERM xterm

WORKDIR /workspace
COPY / /workspace/vota
RUN sudo chown -R votinguser:votinguser /workspace && \
	curl -O https://download.java.net/java/GA/jdk18.0.2.1/db379da656dc47308e138f21b33976fa/1/GPL/openjdk-18.0.2.1_linux-aarch64_bin.tar.gz && \
	tar xvzf openjdk-18.0.2.1_linux-aarch64_bin.tar.gz && \
	rm ./openjdk-18.0.2.1_linux-aarch64_bin.tar.gz
RUN export JAVA_HOME=/workspace/jdk-18.0.2.1 && \
	export PATH=$PATH:/workspace/jdk-18.0.2.1/bin && \
	cd vota && ./gradlew build -x test
RUN rm -Rf /home/votinguser/.gradle && \
	rm /workspace/vota/build/libs/history-*-plain.jar && \
	mv /workspace/vota/build/libs/history-*.jar /workspace/vota.jar && \
	mv /workspace/vota/build/resources/main/docker/startmongo.sh /workspace/startmongo.sh && \
	chmod 777 /workspace/startmongo.sh && \
	rm -Rf /workspace/vota

CMD /workspace/startmongo.sh && \
	/workspace/jdk-18.0.2.1/bin/java -jar /workspace/vota.jar --server.port=8280 --spring.profiles.active=docker && \
	tail -f /dev/null
