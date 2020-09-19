<?php
$db_server = "sophia.cs.hku.hk";
$db_user = "yxwang2";
$db_pwd = "19960201";
$conn = mysqli_connect($db_server, $db_user, $db_pwd, $db_user) or die(mysqli_error());
echo "connect";
$action = (isset($_GET['action']) ? $_GET['action'] : "");
$userID = (isset($_GET['userID']) ? $_GET['userID'] : "");
$type = (isset($_GET['type']) ? $_GET['type'] : "");
$account = (isset($_GET['account']) ? $_GET['account'] : "");
$category = (isset($_GET['category']) ? $_GET['category'] : "");
$cost = (isset($_GET['cost']) ? $_GET['cost'] : "");
$note = (isset($_GET['note']) ? $_GET['note'] : "");
$makeyear = (isset($_GET['y']) ? $_GET['y'] : "");
$makemonth = (isset($_GET['m']) ? $_GET['m'] : "");
$makeday = (isset($_GET['d']) ? $_GET['d'] : "");
$makedate = (isset($_GET['makedate']) ? $_GET['makedate'] : "");



if ($action == "insert" && $cost) {
	/*get currency and rate*/
	$sql = "SELECT remaining,currency, rate FROM account_tb WHERE userID='".$userID."' AND account='".$account."'";
	$result = mysqli_query($conn, $sql)or die(mysqli_error());
	echo $sql;
	if (mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_assoc($result)) {
			$remaining=$row['remaining'];
			$currency=$row['currency'];
			$rate=$row['rate'];
			echo $row["currency"]. $row["rate"];
		}
	} else {
		echo "0 result";
	}
	//insert
	$sql = "INSERT INTO basic_tb (userID,Type,account,category,cost,currency,rate,note,makeyear,makemonth,makeday,makeDate)
	VALUES ('$userID','$type','$account','$category','$cost','$currency','$rate','$note','$makeyear','$makemonth','$makeday','$makedate');";
	$res = mysqli_query($conn, $sql) or die(mysqli_error());
	//update remaining
	$remaining=$remaining-$cost;
	$sql = "UPDATE account_tb SET remaining=".$remaining." WHERE userID='".$userID."' AND account='".$account."'";
	mysqli_query($conn, $sql);
}
mysqli_close($conn);
?>