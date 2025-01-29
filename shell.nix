{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "maven-env";

  buildInputs = [
    pkgs.maven
#    pkgs.mosquitto
# Uncomment line with mosquitto when it's needed
  ];
}
