# DAT255 - Software Engineering Project

## Group Members

| Name | Handle | Email |
| ---- | ------ | ----- |
| Linus Berglund | [Zombax](https://github.com/zombax) | [belinus@student.chalmers.se](mailto:belinus@student.chalmers.se) |
| Johannes Edenholm | [johede3](https://github.com/johede3) | [edenholm@student.chalmers.se](mailto:edenholm@student.chalmers.se) |
| Hugo Frost | [FraousMaster](https://github.com/FraousMaster) | [fhugo@student.chalmers.se](mailto:fhugo@student.chalmers.se) |
| Hamza Kadric | [kadric94](https://github.com/kadric94) | [hamzak@student.chalmers.se](mailto:hamzak@student.chalmers.se) |
| Erik Källberg | [kirez](https://github.com/kirez) | [kalerik@student.chalmers.se](mailto:kalerik@student.chalmers.se) |
| Johan Svennungsson | [knya](https://github.com/knya) | [johsvenn@student.chalmers.se](mailto:johsvenn@student.chalmers.se) |
| Rikard Teodorsson | [hej2010](https://github.com/hej2010) | [rikardt@student.chalmers.se](mailto:rikardt@student.chalmers.se) |
| Timmy Truong | [tagonice](https://github.com/tagonice) | [timmyt@student.chalmers.se](mailto:timmyt@student.chalmers.se) |
| Arvid Wiklund | [BaDaam](https://github.com/BaDaam) | [arvidwi@student.chalmers.se](mailto:arvidwi@student.chalmers.se) |
| Karl Ängermark | [angermark](https://github.com/angermark) | [karlang@student.chalmers.se](mailto:karlang@student.chalmers.se) |

## Useful links

* [Course repo](https://github.com/hburden/DAT255/)
* [Markdown cheat sheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

## How to setup a local virtual CAN interface

make install will install to system and require root permissions.
Only tested on linux will not work on windows but might work on mac


```shell
git clone https://github.com/linux-can/can-utils
cd can-utils
./autogen.sh
make
make install
```

from [StackOverflow](https://stackoverflow.com/questions/21022749/how-to-create-virtual-can-port-on-linux-c)

```shell
modprobe can
ip link add dev vcan0 type vcan
ip link set up vcan0
```

If everything worked as intended it is now possible to interact with vcan0 as if it were a can bus using can-utils
like so:

```shell
cangen vcan0 &
candump vcan0
```

## Running CAN dump extracting script

1. Start WIFI hot spot
2. Power on Moped
3. Edit REMOTE variable in getdump.sh so that it is equal to ip leased to Moped
4. Connect to WIFI hot spot
5. Make sure that current directory is src
6. ./getdump.sh
7. Pray
8. Profit!? (can0.dump + can1.dump in src if success)
