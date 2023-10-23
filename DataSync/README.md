# 基于webhook和openAPI的跨项目数据同步
## 流程
1. 在源项目配置智能助理，当数据发生变化，执行调用webhook动作。
2. 配置好接收同步的webhook
3. 部署webhook消息接收服务，监听webhook事件，根据场景调用openAPI，将数据加工处理同步到目标项目

## 配置说明
```properties
#客户端ID
app.config.openApi.clientId=
#密钥
app.config.openApi.secretKey=
#openAPI的访问地址前缀
app.config.openApi.host=https://ligai.cn/openapi/api/
#项目数据同步映射配置
app.config.syncMappingPath=classpath:syncMapping.json
```

参考JSON：
```json
[{
    "sourceProjectId": 23542065,
    "targetProjectId": 167019207,
    "issueTypeMappings":[
      {
        "sourceTypeId":"23542262",
        "targetTypeId": 167019337,
      "statusMapping":{
        "sourceField": "status",
        "targetField": "status",
        "optionMapping": {
          "23542141":167019286,
          "23542142":167019287,
          "23542143":167019288,
          "23542144":167019289,
          "23542145":167019290
        }
      },
        "conditions":[
          {
            "fieldCode": "customfield_171056672",
            "value": 171056676
          }
        ]
      },
      {
        "sourceTypeId":"23542262",
        "targetTypeId": 167061392,
        "statusMapping":{
          "sourceField": "status",
          "targetField": "status",
          "optionMapping": {
            "23542141":167019286,
            "23542142":167019287,
            "23542143":167019288,
            "23542144":167019289,
            "23542145":167019290
          }
        },
        "conditions":[
          {
            "fieldCode": "customfield_171056672",
            "value": 171056677
          }
        ]
      }
    ],
    "fieldMappingConfigs": [
      {
        "sourceField": "summary",
        "targetField": "summary"
      },
      {
        "sourceField": "startDate",
        "targetField": "startDate"
      },
      {
        "sourceField": "dueDate",
        "targetField": "dueDate"
      },
      {
        "sourceField": "owner",
        "targetField": "owner"
      },
      {
        "sourceField": "estimatePoint",
        "targetField": "estimatePoint"
      },
      {
        "sourceField": "priority",
        "targetField": "priority"
      },
      {
        "sourceField": "follows",
        "targetField": "follows"
      },
      {
        "sourceField": "assignee",
        "targetField": "assignee"
      },
      {
        "sourceField": "description",
        "targetField": "description"
      },
      {
        "sourceField": "customfield_167039258",
        "targetField": "customfield_167039171"
      },
      {
        "sourceField": "updateBy",
        "targetField": "customfield_167039164",
        "single":true
      },
      {
        "sourceField": "customfield_167039248",
        "targetField": "customfield_167039151"
      },
      {
        "sourceField": "customfield_167029241",
        "targetField": "customfield_167039135",
        "optionMapping":{
          "167029245":167039139,
          "167029246":167039140
        }
      },
      {
        "sourceField": "customfield_167029225",
        "targetField": "customfield_167039120",
        "optionMapping":{
          "167029229":167039124,
          "167029230":167039125
        },
        "single":true
      },
      {
        "sourceField": "customfield_167029234",
        "targetField": "customfield_167039127",
        "optionMapping":{
          "167029238":167039131,
          "167029239":167039132
        },
        "single":true
      },
      {
        "sourceField": "customfield_167029219",
        "targetField": "customfield_167039113"
      },
      {
        "sourceField": "customfield_167029214",
        "targetField": "customfield_167039108"
      },
      {
        "sourceField": "customfield_167029209",
        "targetField": "customfield_167039101"
      },
      {
        "sourceField": "customfield_167029197",
        "targetField": "customfield_167039091"
      },
      {
        "sourceField": "customfield_167029202",
        "targetField": "customfield_167039096"
      },
      {
        "sourceField": "customfield_167029192",
        "targetField": "customfield_167039084"
      },
      {
        "sourceField": "customfield_167039241",
        "targetField": "customfield_167039146"
      }
    ]
  }]
```
配置JSON的具体含义参考类：com.zhikan.customize.model.SyncMappingConfig

主要目的是将项目、项目的工作类型、工作类型下的每个字段进行映射配置。