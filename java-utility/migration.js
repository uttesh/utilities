var entityData;

function migrate(inputRequest) {
	entityData = JSON.parse(inputRequest.entityData);
	var fn = new Function('', inputRequest.migrationScript);
	print('fn :: ' + fn)
	return JSON.stringify(fn());
}
