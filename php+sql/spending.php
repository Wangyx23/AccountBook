<?php
$db_server = "sophia.cs.hku.hk";
$db_user = "yxwang2";
$db_pwd = "19960201";
$conn = mysqli_connect($db_server, $db_user, $db_pwd, $db_user) or die(mysqli_error());

$action = (isset($_GET['action']) ? $_GET['action'] : "");
$userID = (isset($_GET['userID']) ? $_GET['userID'] : "");
$year = (isset($_GET['year']) ? $_GET['year'] : "");
$month = (isset($_GET['month']) ? $_GET['month'] : "");

if ($action == "select") {
	echo '{';
	$type = array();
	$category=array();
	$cost = array();
	$note=array();
	$currency = array();
	$rate=array();
	$makedate=array();
	$sql = "SELECT Type,category,cost,note,currency,rate,makedate FROM basic_tb WHERE userID='".$userID."' AND makeyear='".$year."' AND makemonth='".$month."' ORDER BY makedate DESC,createtime DESC";
	
	$res = mysqli_query($conn, $sql) or die(mysqli_error());
	if (mysqli_num_rows($res) > 0) {
		while($row = mysqli_fetch_assoc($res)) {
			array_push($type, $row['Type']);
			array_push($category, $row['category']);
			array_push($cost, $row['cost']);
			array_push($note, $row['note']);
			array_push($currency, $row['currency']);
			array_push($rate, $row['rate']);
			array_push($makedate, $row['makedate']);
		}
	} else {
		
	}
	echo '"Type":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $type[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"category":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $category[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"cost":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $cost[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"note":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $note[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"currency":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $currency[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"rate":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $rate[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo ',"makedate":[';
	$add_delimiter = false;
	for ($i=0; $i<count($type); $i++) {
	echo ($add_delimiter ? ', ' : '') . '"' . $makedate[$i] . '"';
	$add_delimiter = true;
	}
	echo ']';
	echo '}';
}
mysqli_close($conn);
?>