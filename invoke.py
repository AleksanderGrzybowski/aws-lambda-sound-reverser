#! /usr/bin/python3

# This whole wrapper is a workaround for "Argument list too long" bash/zsh error.
# We can't use AWS CLI directly, because --payload parameter must be a command line argument.

import boto3
import base64

client = boto3.client('lambda')
INPUT_FILE='./sator_reversed.wav'
OUTPUT_FILE='./output.wav'
FUNCTION_NAME='reverse-audio'

print('Reading file ' + INPUT_FILE + '...')
input_data = open(INPUT_FILE, 'rb').read()

print('Triggering function...')
payload = "\"" + base64.b64encode(input_data).decode('utf-8') + "\""
response = client.invoke(FunctionName=FUNCTION_NAME, Payload=payload)

with open(OUTPUT_FILE, 'wb') as output_file:
    output_payload = response['Payload'].read().replace(b'"', b'')
    output_data = base64.b64decode(output_payload)
    output_file.write(output_data)
    print('Wrote output file ' + OUTPUT_FILE + '.')
