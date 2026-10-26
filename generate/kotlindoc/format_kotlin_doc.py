#!/usr/bin/env python3
#
#   format_kotlin_doc.py  -  Don Cross <cosinekitty@gmail.com>
#
#   A custom utility for post-processing Markdown documentation
#   for the Kotlin version of Astronomy Engine.
#   There are a few things about Dokka's Markdown (GFM) output I don't like.
#   This script is a hack to work around them.
#
import sys
import os
import re
import shutil


# Members that the Kotlin compiler generates for every enum class.
# They are unhelpful noise in the docs, so they are stripped out.
EnumPropertyNoise = set(['name', 'ordinal', 'entries'])
EnumFunctionNoise = set(['valueOf', 'values'])
EnumNoisePages = set(['entries.md', 'value-of.md', 'values.md'])


def RemovePlatformTags(text):
    # Every declaration lives in commonMain, so the [common] platform tag
    # that Dokka puts on each one adds no information. ([jvm] was the
    # equivalent tag back when the library was JVM-only.)
    fix = text
    for tag in ['[common]', '[jvm]']:
        fix = fix.replace(' | ' + tag, '')
        fix = fix.replace(tag + '<br>', '')
        fix = fix.replace(tag + '\\\n', '')
    fix = fix.replace('\njvm\n', '')
    return fix


def RemoveSectionRows(text, heading, unwanted):
    # Within the Markdown table under `heading`, delete the rows whose first
    # cell links to one of the names in `unwanted`. If that leaves the table
    # empty, delete the entire section.
    prefix = '## ' + heading + '\n'
    front = text.find(prefix)
    if front < 0:
        return text
    back = text.find('\n## ', front + len(prefix))
    back = len(text) if back < 0 else back + 1
    lines = text[front:back].split('\n')
    kept = [r for r in lines if not (m := re.match(r'^\| \[([^\]]+)\]', r)) or m.group(1) not in unwanted]
    if not any(re.match(r'^\| \[', r) for r in kept):
        return text[:front] + text[back:]
    return text[:front] + '\n'.join(kept) + text[back:]


def RemoveEnumBoilerplate(text):
    # `name` and `ordinal` appear on enum entries, and `entries`, `valueOf`,
    # and `values` appear on enum classes. None of them tell the reader anything.
    # The `name`/`ordinal` links also point at the wrong class:
    # https://github.com/Kotlin/dokka/issues/2464
    fix = RemoveSectionRows(text, 'Properties', EnumPropertyNoise)
    if '\nenum [' in fix:
        fix = RemoveSectionRows(fix, 'Functions', EnumFunctionNoise)
    return fix


def FixMarkdown(text):
    fix = RemoveEnumBoilerplate(text)
    fix = RemovePlatformTags(fix)
    return fix.rstrip('\n') + '\n'


def ExtractVersionNumber(filename):
    # Look for a line like this in the build.gradle.kts file:
    # version = "2.1.0"
    # This is the current version of Astronomy Engine.
    with open(filename, "rt") as infile:
        for line in infile:
            m = re.match(r'^\s*version\s*=\s*"([^"]+)"\s*$', line)
            if m:
                return m.group(1)
    print('ERROR: Cannot find version number in file:', filename)
    return None

# A typical line looks like this:
# | [Direction](-direction/index.md) | [jvm]<br>enum [Direction](-direction/index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[Direction](-direction/index.md)&gt; <br>Selects whether to search for a rising event or a setting event for a celestial body. |
# Find all the links like (-direction/index.md) and prefix them with 'doc/':
# [Direction](doc/-direction/index.md)
# This is probably one of the most horrible regular expressions
# I have ever crafted. One tricky bit is the use of [^\):].
# Apart from looking like a surreal emoji, the ':' prevents
# substitution of external hyperlinks like
# https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html
LinkRegex = re.compile(r'\[([^\]]+)\]\(([^\):]+)\)')     # [Direction](-direction/index.md)

if __name__ == '__main__':
    prefixFileName = 'kotlin_prefix.md'
    inDocDir = '../../source/kotlin/build/dokka/markdown/astronomy/io.github.cosinekitty.astronomy'
    rootFileName = os.path.join(inDocDir, 'index.md')
    outDir = '../../source/kotlin'
    outDocDir = os.path.join(outDir, 'doc')
    outFileName = os.path.join(outDir, 'README.md')
    version = ExtractVersionNumber(os.path.join(outDir, 'build.gradle.kts'))
    if version is None:
        sys.exit(1)

    with open(outFileName,'wt') as outfile:
        # Copy the entire prefix file, but keep the version number updated.
        with open(prefixFileName, 'rt') as infile:
            text = infile.read()
            text = text.replace("[ASTRONOMY_ENGINE_VERSION]", version)
            outfile.write(text)

        # Slurp in the interesting parts of the documentation root.
        with open(rootFileName,'rt') as infile:
            # Skip unwanted junk at the top.
            # Start paying attention after the package declaration.
            skipJunk = True
            for line in infile:
                if skipJunk:
                    if line.startswith('# Package'):
                        skipJunk = False
                else:
                    fix = LinkRegex.sub(r'[\1](doc/\2)', line)
                    fix = RemovePlatformTags(fix)
                    outfile.write(fix)

    # Nuke the existing 'doc' directory.
    shutil.rmtree(outDocDir, ignore_errors = True)

    # Recursively process files, reading from the raw files generated by Dokka,
    # and writing to the target directory to be hosted on GitHub.
    for dirName, childDirs, files in os.walk(inDocDir):
        # Calculate the target relative path.
        targetDir = os.path.join(outDocDir, dirName[1+len(inDocDir):])
        os.makedirs(targetDir, exist_ok = True)
        # Skip pages for compiler-generated enum members; RemoveEnumBoilerplate
        # already removed every link to them.
        isEnum = False
        if 'index.md' in files:
            with open(os.path.join(dirName, 'index.md'), 'rt') as infile:
                isEnum = '\nenum [' in infile.read()
        for fn in files:
            if isEnum and fn in EnumNoisePages:
                continue
            if fn.endswith('.md'):
                inFileName = os.path.join(dirName, fn)
                with open(inFileName, 'rt') as infile:
                    text = infile.read()
                outFileName = os.path.join(targetDir, fn)
                with open(outFileName, 'wt') as outfile:
                    fix = FixMarkdown(text)
                    outfile.write(fix)

    sys.exit(0)
