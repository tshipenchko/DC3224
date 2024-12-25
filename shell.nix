{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "maven-env";

  buildInputs = [
    pkgs.maven
    pkgs.mosquitto
  ];
}
