# corda-iou-spring-boot
Spring boot with the corda IOU example

### Intellij Project Setup
1. Setup the project as a gradle project
2. Add the cordapp-example-0.1.jar to your classpath
- Click on File -> Project Structure
- Select the corda main module and click on Dependencies tab
- Click on the + icon on the right and select the 'lib' folder

### Run Project
1. Startup the IOU parties and notary from the [IOU example V3 release](https://github.com/corda/cordapp-example)
2. Execute the spring boot run command via gradle
```
./gradlew bootRun
```
3. Use postman or similar tool to post to localhost:8080/api/iou?iouValue=1&partyName=O=PartyB, L=New York, C=US
- Check ious at localhost:8080/api/ious