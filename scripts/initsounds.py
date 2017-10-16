#!/usr/bin/env python
"""
Generates SQL statements to initialize the Hydra sounds table.

Usage:

    ./scripts/initsounds.py | psql -U HydraBot
"""

import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument('-F', '--flush', action='store_true',
  help='Flush the table before adding new entries.')
parser.add_argument('-d', '--directory',
  help='The directory where all the sound files reside. Defaults to '
       'AudioFiles/ in the current working directory.')


def main():
  args = parser.parse_args()
  args.directory = os.path.abspath(args.directory or 'AudioFiles')
  if args.flush:
    print('DELETE FROM ONLY HydraBotDB.Sounds;')
    print()

  # TODO: Escape values to prevent malicious statements
  # TODO: Where to load the description from?
  for fname in os.listdir(args.directory):
    if not fname.endswith('.wav'): continue
    print("INSERT INTO HydraBotDB.Sounds (name, path, amount_requests, description)\n"
          "VALUES ('{name}', '{path}', 0, '')\n"
          "ON CONFLICT (name) DO UPDATE\n"
          "  SET path = '{path}';"
        .format(name=fname[:-4], path=os.path.join(args.directory, fname)))
    print()

if __name__ == '__main__':
  main()
