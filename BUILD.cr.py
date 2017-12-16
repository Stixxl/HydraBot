
import craftr from 'craftr'
import java from 'craftr/lang/java'

java.prebuilt(
  name = 'deps',
  artifacts = [
    'com.moandjiezana.toml:toml4j:0.7.2',
    'commons-cli:commons-cli:1.3.1',
    'com.github.austinv11:Discord4j:2.9.3',
    #'ch.qos.logback:logback-classic:1.1.9', # TODO: Can't be resolved by Craftr :-(
    'org.postgresql:postgresql:9.2-1002-jdbc4'
  ]
)

java.binary(
  name = 'hydra',
  deps = [':deps'],
  srcs = craftr.glob('src/**/*.java'),
  src_roots = ['src/main/java'],
  main_class = 'com.stiglmair.hydra.main.Main',
  dist_type = 'merge'
)

java.run(':hydra')
