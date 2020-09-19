<?php
$db_server = "sophia.cs.hku.hk";
$db_user = "yxwang2";
$db_pwd = "19960201";
$conn = mysqli_connect($db_server, $db_user, $db_pwd, $db_user) or die(mysqli_error());

$action = (isset($_GET['action']) ? $_GET['action'] : "");
$userID = (isset($_GET['userID']) ? $_GET['userID'] : "");

$sql = "SELECT rate FROM exchange_rate WHERE userID='".$userID."' AND currency='HKD'";
$res = mysqli_query($conn, $sql) or die(mysqli_error());
if (mysqli_num_rows($res) > 0) {
	while($row = mysqli_fetch_assoc($res)) {
		$rate=$row['rate'];
		}
	}else {}
	
if ($action == "select") {
	$year = (isset($_GET['year']) ? $_GET['year'] : "");
	$month = (isset($_GET['month']) ? $_GET['month'] : "");
	
	echo '{';
	$account = array();
	$remaining=array();
	$currency = array();
	$sql = "SELECT account,remaining,currency FROM account_tb WHERE userID='".$userID."'";
	
	$res = mysqli_query($conn, $sql) or die(mysqli_error());
	if (mysqli_num_rows($res) > 0) {
		while($row = mysqli_fetch_assoc($res)) {
			
			array_push($account, $row['account']);
			array_push($remaining, $row['remaining']);
			array_push($currency, $row['currency']);
		}
	} else {
		
	}
	echo '"rate":"' . $rate . '"';
	echo ',"account":[';
	$add_delimiter = false;
	for ($i=0; $i<count($account); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $account[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"remaining":[';
	$add_delimiter = false;
	for ($i=0; $i<count($remaining); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $remaining[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"currency":[';
	$add_delimiter = false;
	for ($i=0; $i<count($currency); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $currency[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo '}';
}
if ($action == "update") {
	$account = (isset($_GET['account']) ? $_GET['account'] : "");
	$remain = (isset($_GET['remain']) ? $_GET['remain'] : "");
	$currency = (isset($_GET['currency']) ? $_GET['currency'] : "");
	if($currency=="HKD"){
		$rate=$rate;
	}else{
		$rate=1;
	}
	$sql = "UPDATE account_tb SET remaining=".$remain.", rate=".$rate.", currency='".$currency."' WHERE userID='".$userID."' AND account='".$account."'";
	echo $sql;
	mysqli_query($conn, $sql);
}
mysqli_close($conn);
?>