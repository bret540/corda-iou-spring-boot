# corda-iou-spring-boot
Spring boot with the corda IOU example

### Project Setup
1. Pull in the dependencies from maven
2. Add the cordapp-example-0.1.jar to your classpath
3. Setup a spring boot configuration and start the ServicesApplication
4. Startup the IOU parties and notary
5. Use a network simulator to POST some data (localhost:8080/api/iou?iouValue=3&partyName=O=PartyB, L=New York, C=US)
