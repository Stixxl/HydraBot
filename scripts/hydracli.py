#!/usr/bin/env python
"""
Execute a HydraBot command via the command-line.
"""

from __future__ import print_function

import argparse
import errno
import json
import os
import sys

try:
  from urllib.request import urlopen
  from urllib.parse import urlencode
  from urllib.error import HTTPError
except ImportError:
  from urllib2 import urlopen, HTTPError
  from urllib import urlencode

try:
  input = raw_input
except NameError:
  pass


parser = argparse.ArgumentParser()
parser.add_argument('command', nargs='...')
parser.add_argument('-c', '--config', action='store_true')
parser.add_argument('-t', '--token')
parser.add_argument('-u', '--user-id')
parser.add_argument('-H', '--host')

cfg_filename = os.path.expanduser('~/.hydracli')


def load_config():
  try:
    with open(cfg_filename) as fp:
      result = json.load(fp)
  except OSError as e:
    if e.errno != errno.ENOENT:
      raise
    result = {}
  result.setdefault('host', 'hydra.stiglmair.com')
  return result


def do_config():
  try:
    data = load_config()
  except ValueError as e:
    print('warning: (loading "{}")'.format(cfg_filename), e)
    data = {}

  def ask(key, title):
    info = ' [{}]'.format(data[key]) if data.get(key) not in (None, '') else ''
    value = input('{}{}: '.format(title, info))
    if value == ' ':  # delete the entry when a space is entered
      del data[key]
      value = None
    else:
      value = value.strip() or data.get(key)
    if value not in (None, ''):
      data[key] = value

  ask('user_id', 'Discord User ID')
  ask('token', 'User Access Token')
  ask('host', 'HydraBot Host')

  data = {k: v for k, v in data.items() if v}
  with open(cfg_filename, 'w') as fp:
    json.dump(data, fp, indent=2)


def main():
  args = parser.parse_args()
  if args.config:
    do_config()
    return 0

  config = load_config()

  if not args.token: args.token = config.get('token')
  if not args.user_id: args.user_id = config.get('user_id')
  if not args.host: args.host = config.get('host')

  if not args.token:
    print('fatal: no token specified, pass --token argument')
    return 1
  if not args.user_id:
    print('fatal: no user id configured, pass --user-id argument')
    return 1

  if not args.host.startswith('http://') and not args.host.startswith('https://'):
    args.host = 'https://' + args.host

  query = {'command': ' '.join(args.command), 'userId': args.user_id}
  if args.token:
    query['token'] = args.token

  url = '{}/commands?{}'.format(args.host, urlencode(query))
  print('GET', url)
  try:
    response = urlopen(url)
  except HTTPError as e:
    print('error:', e)
    print(e.read().decode('utf8'))
    return 2
  else:
    print(response.read().decode('utf8'))
    pass


if __name__ == '__main__':
  sys.exit(main())
