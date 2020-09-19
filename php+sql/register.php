<?php
$db_server = "sophia.cs.hku.hk";
$db_user = "yxwang2";
$db_pwd = "19960201";
$conn = mysqli_connect($db_server, $db_user, $db_pwd, $db_user) or die(mysqli_error());

$action = (isset($_GET['action']) ? $_GET['action'] : "");
$userID = (isset($_GET['userID']) ? $_GET['userID'] : "");
$pwd = (isset($_GET['pwd']) ? $_GET['pwd'] : "");



if ($action == "insert") {
	
	//insert
	
	$sql = "INSERT INTO user_tb (userID,pwd)
	VALUES ('$userID','$pwd');";
	$sql .= "INSERT INTO `account_tb` ( userID,account, remaining,currency,rate)
	VALUES ('$userID','Cash',0,'HKD',0.9);";
	$sql .="INSERT INTO `account_tb` ( userID,account, remaining,currency,rate)
	VALUES ('$userID','Deposit',0,'CNY',1);";
	$sql .= "INSERT INTO `account_tb` ( userID,account, remaining,currency,rate)
	VALUES ('$userID','Alipay',0,'CNY',1);";
	$sql .= "INSERT INTO `account_tb` ( userID,account, remaining,currency,rate)
	VALUES ('$userID','WeChat',0,'CNY',1);";
	$sql .= "INSERT INTO `account_tb` ( userID,account, remaining,currency,rate)
	VALUES ('$userID','Ant Check Later (AliPay)',0,'CNY',1);";
	$sql .= "INSERT INTO `account_tb` ( userID,account, remaining,currency,rate)
	VALUES ('$userID','Credit Card',0,'CNY',1);";
	$sql .= "INSERT INTO `exchange_rate` (userID,currency,rate)
	VALUES ('$userID','HKD',0.9);";
	$sql .= "INSERT INTO `exchange_rate` (userID,currency,rate) VALUES
	('$userID','CNY',1);";
	echo '{';
	if (mysqli_multi_query($conn, $sql)) {
		echo '"s":"success"';
	} else {
		echo '"s":"error"';
	}
	echo '}';
}
mysqli_close($conn);
?>