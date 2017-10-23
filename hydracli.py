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
except ImportError:
  from urllib2 import urlopen
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

  info = ' [{}]'.format(data['user_id']) if data.get('user_id') else ''
  data['user_id'] = input('Discord User ID{}: '.format(info)) or data['user_id']

  info = ' [{}]'.format(data['token']) if data.get('token') else ''
  data['token'] = input('User Access Token{}: '.format(info)) or data['token']

  info = ' [{}]'.format(data['host']) if data.get('host') else ''
  data['host'] = input('HydraBot Host{}: '.format(info)) or data['host']

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

  # if not args.token: pass
  if not args.user_id:
    print('fatal: no user id configured, pass --user-id argument')
    return 1

  query = {'command': ' '.join(args.command), 'userId': args.user_id}
  url = 'https://{}/commands?{}'.format(args.host, urlencode(query))
  print('GET', url)
  response = urlopen(url)
  if response.getcode() != 204:
    print('error: status code', response.getcode())
    return 1


if __name__ == '__main__':
  sys.exit(main())
