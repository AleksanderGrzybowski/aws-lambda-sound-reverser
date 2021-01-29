Simple demo of running Java on AWS Lambda, inspired by Tenet :) This function reverses WAV files. Sample file is inverted Sator talking in reverse. 

Run `./gradlew buildZip && terraform apply`, then `./invoke.py`. Result can be seen in `output.wav` file.

