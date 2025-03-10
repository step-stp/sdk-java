# Stratumn SDK for Java

The official Stratumn SDK for Java to interact with [Trace](https://trace.stratumn.com).

## :satellite: Installing

### Maven

<dependency>
  <groupId>com.stratumn.sdk</groupId>
  <artifactId>sdk-java</artifactId>
  <version>0.0.8</version>
</dependency>


## :rocket: Usage and Getting Started

### Configuration

You must start by importing the `Sdk` class:

```sh
import com.stratumn.sdk.Sdk;
```

You can then create a new instance of the `Sdk`:

```sh
  Secret s = Secret.NewPrivateKeySecret(YOUR_SECRETS.privateKey);
  SdkOptions opts = new SdkOptions(YOUR_CONFIG.workflowId, s);
```

You will need to provide:

- a valid workflow id that has been created via [Trace](https://trace.stratumn.com).
- a secret that will be used to authenticate via [Account](https://account.stratumn.com)

The authentication secret can be one of the following:

- a `CredentialSecret` object containing the email and password of the account
- a `PrivateKeySecret` object containing the signing private key of the account

Notes:

- You can find the workflow id in the url of your workflow. For example, when looking at `https://trace.stratumn.com/workflow/95572258`, the id is `95572258`.
- When a `PrivateKeySecret` is provided, a unique message is generated, signed and sent to [Account](https://account.stratumn.com) for validation. We check that the signature and the message are valid and return an authentication token in that case.
- By default the `Sdk` is configured to point to the production environment of Trace. During a development phase, you can configure the `Sdk` to point to the staging environment:

```
Secret s = Secret.NewPrivateKeySecret(YOUR_SECRETS.privateKey);
SdkOptions opts = new SdkOptions(YOUR_CONFIG.workflowId, s);
opts.setEndpoints(new Endpoints("https://account-api.staging.stratumn.com", "https://trace-api.staging.stratumn.com", "https://media-api.staging.stratumn.com"));
```
- To enable low level http debuging set the enableDebugging option to true;

```
opts.setEnableDebuging(true);
```

- To connect through a proxy server: 

```
opts.setProxy("MyProxyHost", 1234);
```

Finally to create the sdk instance:

```
Sdk<MyStateType> sdk = new Sdk<MyStateType>(opts, MyStateType.class);
```


### Creating a new trace

You can create a new trace this way:

```
 Map<String, Object> data = new HashMap<String, Object>();
 data.put("weight", "123");
 data.put("valid", true);
 data.put("operators", new String[]{"1", "2" });
 data.put("operation", "my new operation 1"); 
 NewTraceInput<Object> newTraceInput = new 
 NewTraceInput<Object>(YOUR_CONFIG.formId, data); 
 TraceState<Object, Object> state = sdk.newTrace(newTraceInput);

 
```

You must provide:

- `formId`: a valid form id,
- `data`: the data object corresponding to the action being done.

The Sdk will return an object corresponding to the "state" of your new trace. This state exposes the following fields:

- `traceId`: the id (uuid format) which uniquely identify the newly created trace,
- `headLink`: the link that was last appended to the trace,
- `updatedAt`: the `Date` at which the trace was last updated,
- `updatedBy`: the id of the user who last updated the trace,
- `data`: the aggregated data modeling the state the trace is in.

Notes:

- You can view your forms detail from your group's Attestation Forms page (for ex `https://trace.stratumn.com/group/322547/forms`).
- When viewing a specific form detail, you can retrieve the form id from the url. (`https://trace.stratumn.com/group/322547/form/788547` => `formId=788547`).
- The `data` object argument must be valid against the JSON schema of the form you are using, otherwise Trace will throw a validation error.


### Appending a link to an existing trace
  
```
AppendLinkInput<Object> appLinkInput = new AppendLinkInput<Object>(YOUR_CONFIG.formId, data, prevLink);
TraceState<Object, Object> state =   sdk.appendLink(appLinkInput);

```
If you don't have access to the head link, you can also provide the trace id:

```
AppendLinkInput<Object> appLinkInput = new AppendLinkInput<Object>(YOUR_CONFIG.formId, data, traceId);
TraceState<Object, Object> state =   sdk.appendLink(appLinkInput);
```
 
You must provide:

- formId: a valid form id,
- data: the data object corresponding to the action being done,
- prevLink or traceId.

The Sdk will return the new state object of the trace. The shape of this object is the same as explained [previously](#creating-a-new-trace).

Notes:

- You can view your forms detail from your group's Attestation Forms page (for ex `https://trace.stratumn.com/group/322547/forms`).
- When viewing a specific form detail, you can retrieve the form id from the url. (`https://trace.stratumn.com/group/322547/form/788547` => `formId=788547`).
- The `data` object argument must be valid against the JSON schema of the form you are using, otherwise Trace will throw a validation error.

### Requesting the transfer of ownership of a trace

You can "push" the trace to another group in the workflow this way:

```
Map<String, Object> data =new HashMap<String, Object>( Collections.singletonMap("why", "because I'm testing the pushTrace 2"));

PushTransferInput<Object> push = new PushTransferInput<Object>(recipient, data, prevLink);
someTraceState = sdk.pushTrace<Object>(push);

```


The arguments are:

- `recipient`: the id of the group to push the trace to,
- `data`: (optional) some data related to the push transfer,
- `prevLink` or `traceId`.

You can also "pull" an existing trace from another group:

``` 
Map<String, Object> data =new HashMap<String, Object>( Collections.singletonMap("why", "because I'm testing the pushTrace 2"));
PullTransferInput<Object> pull = new PullTransferInput<Object>(TraceId, data, prevLink);
TraceState<Object, Object> statepul =  sdk.pullTrace(pull);

```

And in this case, the arguments are:

- `data`: (optional) some data related to the pull transfer,
- `prevLink` or `traceId`.

The Sdk will return the new state object of the trace. The shape of this object is the same as explained [previously](#creating-a-new-trace).

Notes:

- In both cases, the trace is not transferred automatically to or from the group. The recipient must respond to your request as we will see in the [next section](#responding-to-a-transfer-of-ownership-of-a-trace).
- You don't need to provide a `recipient` in the case of a `pullTransfer` since the two parties of the transfer can be inferred (you and the current owner of the trace).
- The `data` object argument is optional. When it is provided, it is a free form object that will not be validated against a JSON schema.

### Responding to a transfer of ownership of a trace

When someone pushed a trace to your group, you can either accept or reject the transfer:

```
TransferResponseInput<Object> trInput = new TransferResponseInput<Object>(null,traceId);
TraceState<Object, Object> stateAccept = sdk.acceptTransfer(trInput);
```

Or:

```
TransferResponseInput<Object> trInput = new TransferResponseInput<Object>(null, traceId);
TraceState<Object, Object> stateReject = sdk.rejectTransfer(trInput);

```

Alternatively, if you have initiated the transfer (push or pull), you can  also cancel before it has been accepted:

```
TransferResponseInput<Object> responseInput = new TransferResponseInput<Object>(null, traceId);
TraceState<Object, Object> stateCancel = sdk.cancelTransfer(responseInput);

```

In all cases, the arguments are:

- `data`: (optional) some data related to the pull transfer,
- `prevLink` or `traceId`.

The Sdk will return the new state object of the trace. The shape of this object is the same as explained [previously](#creating-a-new-trace).

Notes:

- The `data` object argument is optional. When it is provided, it is a free form object that will not be validated against a JSON schema.

### Trace stages

Your group in the workflow is composed of multiple stages. There are always 3 default stages:

- `Incoming`: this stage lists all the traces that are being transferred to your group (push or pull),
- `Backlog`: this stage lists all the traces that have been transferred to your group and accepted,
- `Outgoing`: this stage lists all the traces that are being transferred to another group (push or pull).

The other stages are called `Attestation` stages. They compose the logic of your group in the context of this workflow.

Notes:

- When someone pushes a trace to your group, it will appear in your `Incoming` stage and their `Outgoing` stage.
- When you accept a transfer, the trace will move to your `Backlog` stage.
- When you reject a transfer, the trace will move back to its previous `Attestation` stage and disappear from the `Outgoing` and `Incoming` stages it was in.

### Retrieving traces

When all you have is the id of a trace, you can get its state by calling:

```
GetTraceStateInput input = new GetTraceStateInput(traceId);
TraceState<Object, Object> state = sdk.getTraceState<Object>(input);
```

The argument:

- `traceId`: the id of the trace

You can also retrieve the links of a given trace this way:

```
GetTraceDetailsInput input = new GetTraceDetailsInput(traceId, first, after, last, before);
TraceDetails<Object> details =  sdk.getTraceDetails<Object>(input);
```

In this case, we are asking for the first 5 links (see [pagination](#pagination)).

Arguments:

- `traceId`: the id of the trace,
- `first`: (optional) retrieve the first n elements,
- `after`: (optional) retrieve the elements after a certain point,
- `last`: (optional) retrieve the last n elements,
- `before`: (optional) retrieve the elements before a certain point.

For more explanation on how the pagination work, go to the dedication [section](#pagination).

The Sdk will return an object with the details about the trace you asked for. This object exposes the following fields:

- `links`: the paginated array of links,
- `totalCount`: the total number of links in the trace,
- `info`: a pagination object (more on this [here](#pagination)).

To retrieve all the traces of a given stage, you can:

```js
Sdk<Object> sdk = GetSdk();
PaginationInfo paginationInfo = new PaginationInfo(first, after, last, before);
TracesState<Object, Object> state =  sdk.getIncomingTraces<Object>(paginationInfo);
```

Or:

```js
 Sdk<Object> sdk = GetSdk();
PaginationInfo paginationInfo = new PaginationInfo(first, after, last, before);
TracesState<Object, Object> state =  sdk.getOutgoingTraces<Object>(paginationInfo);
```

Or:

```js
var sdk = GetSdk();
PaginationInfo info = new PaginationInfo(first, after, last, before);
 TracesState<Object, Object> state = sdk.GetBacklogTraces<Object>(info);
```

Arguments:

- `first`: (optional) retrieve the first n elements,
- `after`: (optional) retrieve the elements after a certain point,
- `last`: (optional) retrieve the last n elements,
- `before`: (optional) retrieve the elements before a certain point.

For more explanation on how the pagination work, go to the dedication [section](#pagination).

The Sdk will return an object with the traces currently in the given stage. This object exposes the following fields:

- `traces`: the paginated array of traces (trace states actually),
- `totalCount`: the total number of traces in the trace,
- `info`: a pagination object (more on this [here](#pagination)).

### Pagination

When a method returns an array of elements (traces, links, etc..), it will be paginated. It means that you can provide arguments to specify how many elements to retrieve from which point in the full list. The pagination arguments will always look like:

- `first`: (optional) retrieve the first n elements,
- `after`: (optional) retrieve the elements after a certain point,
- `last`: (optional) retrieve the last n elements,
- `before`: (optional) retrieve the elements before a certain point.

You must use `first` and/or `after` together, `last` and/or `before` together. If you try to retrieve the `first=n before=xyz` the Sdk will throw an error.

In the result object, you will have the `totalCount` and an `info` object that has the following fields:

- `hasNext`: a flag telling if there is a next series of elements to retrieve after this one,
- `hasPrevious`: a flag telling if there is a previous series of elements to retrieve before this one,
- `startCursor`: (optional) a cursor (string) representing the position of the first element in this series,
- `endCursor`: (optional) a cursor (string) representing the position of the last element in this series.

Let's look at a pagination example. We start by retrieving (and consuming) the first 10 incoming traces:


```
Sdk<Object> sdk = GetSdk();
PaginationInfo paginationInfo = new PaginationInfo(10, null, null, null);
TracesState<Object, Object> results =  sdk.getIncomingTraces<Object>(paginationInfo);
```


Next, we look at the pagination info results to know if there are more traces to retrieve:

```
if (results.Info.HasNext) {
PaginationInfo paginationInfo = new PaginationInfo(10, results.Info.EndCursor, null, null);
TracesState<Object, Object> results =  sdk.getIncomingTraces<Object>(paginationInfo);
}
```




### :floppy_disk: Handling files

When providing a `data` object in an action (via `newTrace`, `appendLink` etc.), you can embed files that will automatically be uploaded and encrypted for you. We provide two ways for embedding files, depending on the platform your app is running.



```
AppendLinkInput<Object> appLinkInput = new AppendLinkInput<Object>(YOUR_CONFIG.formId, data, TraceId);
TraceState<Object, Object> state = sdk.appendLink(appLinkInput);
```
In the browser, assuming you are working with File objects, you can use:

```
Map<String, Object> data = new HashMap<String, Object>();
 data.put("weight", "123");
 data.put("valid", true);
 data.put("operators", new String[]{"1", "2" });
 data.put("operation", "my new operation 1"); 
 
data.Add("Certificate1",FileWrapper.FromFilePath(Path.GetFullPath(filePath)));
data.Add("Certificates", new Identifiable[] { FileWrapper.FromFilePath(filePath});

AppendLinkInput<Object> appLinkInput = new AppendLinkInput<Object>(YOUR_CONFIG.formId, data, TraceId);
TraceState<Object, Object> state = sdk.appendLink(appLinkInput);
```


This record uniquely identifies the corresponding file in our service and is easily serializable. If you look in the `headLink` of the returned state, you will see that the `FileWrapper` have been converted to `FileRecord` types:


When you retrieve traces with the Sdk, it will not automatically download the files for you. You have to explicitely call a method on the Sdk for that purpose:

```
state = sdk.getTraceState<Object>(new GetTraceStateInput(traceId));
Object dataWithRecords = state.HeadLink.FormData();

Object dataWithFiles = sdk.downloadFilesInObject(dataWithRecords);
IDictionary<String, Property<FileWrapper>> fileWrappers = Helpers.extractFileWrappers(dataWithFiles);
for (Property<FileWrapper> fileWrapperProp :  fileWrappers.Values)
{
   WriteFileToDisk(fileWrapperProp.Value);
}
```