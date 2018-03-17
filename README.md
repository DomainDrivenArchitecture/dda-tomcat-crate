# tomcat-crate and liferay-crate
[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-tomcat-crate.svg)](https://clojars.org/dda/dda-tomcat-crate)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-tomcat-crate.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-tomcat-crate)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](https://clojurians.slack.com/messages/#dda-pallet/) | [<img src="https://domaindrivenarchitecture.org/img/meetup.svg" width=50 alt="DevOps Hacking with Clojure Meetup"> DevOps Hacking with Clojure](https://www.meetup.com/de-DE/preview/dda-pallet-DevOps-Hacking-with-Clojure) | [Website & Blog](https://domaindrivenarchitecture.org)

## Compatibility
The crates run and work on
 * pallet 0.8
 * ubuntu 16.04

## Features

This crate installs a tomcat application server on the target machine. Additionally, the dda-liferay-crate can be utilized together with the tomcat installation.

## Usage documentation
This crate is responsible for configuring and installing the tomcat application server. If you desire to use a combination of tomcat and httpd you need to use the dda-httpd-crate together with this crate. The worker.properties and forwarding configuration is done in the dda-httpd-crate.

### Prepare vm
This crate was tested on an installed ubuntu 16.04 installation. 
1. Install ubuntu16.04
2. In some cases update and upgrade can be fix some minor problems. Be sure the remote machine has a running ssh-service.
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install openssh-server
```

### Configuration
The configuration consists of two files defining both WHERE to install the software and WHAT to install and configure.
* `targets.edn`: describes on which target system(s) the software will be installed
* `tomcat.edn`: describes the configuration of the application-server.

Examples of these files can be found in the root directory of this repository.

#### Targets config example
Example content of file `targets.edn`:
```clojure
{:existing [{:node-name "test-vm1"            ; semantic name
             :node-ip "35.157.19.218"}]       ; the ip4 address of the machine to be provisioned
 :provisioning-user {:login "initial"         ; account used to provision
                     :password "secure1234"}} ; optional password, if no ssh key is authorized
```

#### Tomcat config example
Example content of file `tomcat.edn`:
```clojure
{:standalone                                  ; The keyword :standalone specifies a tomcat installation without httpd
  {:xmx-megabyte 512}}                       ; Specifies the maximum heap size.
```         

The tomcat configuration is responsible for configuring and installing tomcat only. If you wish to use it together with liferay see the dda-liferay-crate for configuration and usage. 

#### Use Integration 
The dda-tocmat-crate provides easy access to the required installation and configuration process.
To apply your configuration simply create the necessary files and proceed to the corresponding integration namespace.
For example:
```clojure
(in-ns 'dda.pallet.dda-tomcat-crate.app.instantiate-existing)
(apply-install)
(apply-configure)
```   
This will apply the installation and configuration process to the provided targets defined in targets.edn.

### Watch log for debug reasons
In case of problems you may want to have a look at the log-file:
`less logs/pallet.log`

## Reference
Some details about the architecture: We provide two levels of API. **Domain** is a high-level API with many build in conventions. If this conventions don't fit your needs, you can use our low-level **infra** API and realize your own conventions.

### Domain API

#### Targets
The schema for the targets config is:
```clojure
(def ExistingNode {:node-name Str                   ; your name for the node
                   :node-ip Str                     ; nodes ip4 address       
                   })

(def ProvisioningUser {:login Str                   ; user account used for provisioning / executing tests
                       (optional-key :password) Str ; password, is no authorized ssh key is avail.
                       })

(def Targets {:existing [ExistingNode]              ; nodes to test or install
              :provisioning-user ProvisioningUser   ; common user account on all nodes given above
              })
```
The "targets.edn" uses this schema.

#### Tomcat config
The schema for the tomcat configuration is:
```clojure
(def DomainConfig
  "Represents all possible domain configurations."
  (s/either
    lr/LR6            
    lr/LR7
    standalone/DomainConfig))
```

The domain configuration consists of one of the provided configuration. The schema for the three configs look as follows.

```clojure
(def LR6
  {:lr-6x LrCommon})

(def LR7
  {:lr-7x LrCommon})
```

Please note, the Liferay domain configurations are only usable if you wish to use a liferay together with tomcat. Theey might be usable for other scenarios but will be most likely less than ideal. For a standalone tomcat installation the following shema is applicable.

```clojure
(def DomainConfig
  "Represents the tomcat configuration."
  {:standalone                                
  {(s/optional-key :xmx-megabyte) s/Num}})
```

The domain configurations provided by us derive from our own needs and customers. You are free to create your own domain configuration if you have the need for different configuration options. The complete configuration options are only exposed on an infrastructure level.

### Infra API
The Infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions. The tomcat crate does not use any infrastructure config form any other crate.

## License
Published under [apache2.0 license](LICENSE.md)