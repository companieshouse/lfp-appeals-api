print('LFP appeals API')

docker_compose('../docker-chs-development/modules/lfp-appeals/lfp-appeals-api.docker-compose.yaml')

custom_build(
  ref = '169942020521.dkr.ecr.eu-west-1.amazonaws.com/local/lfp-appeals-api',
  command = 'mvn compile jib:dockerBuild -Dimage=$EXPECTED_REF',
  live_update = [
    sync(
      local_path = './target/classes',
      remote_path = '/app/classes'
    ),
    restart_container()
  ],
  deps = ['./target/classes']
)

local_resource(
  name = 'compile-java',
  cmd = 'mvn compile',
  deps = ['src']
)
