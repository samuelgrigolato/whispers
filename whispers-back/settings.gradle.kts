rootProject.name = "whispers-back"
include("whispers-back-domain")
include("whispers-back-app")
include("whispers-back-api")
include("whispers-back-api:whispers-back-jpa")
findProject(":whispers-back-api:whispers-back-jpa")?.name = "whispers-back-jpa"
include("whispers-back-jpa")
include("whispers-back-server1")
include("whispers-back-dynamo")
include("whispers-back-server2")
include("whispers-back-sns")
