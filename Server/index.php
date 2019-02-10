<?php
$redis = new Redis();
$redis->connect('127.0.0.1', 6379);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    header('Access-Control-Allow-Origin:*');
    header('Content-type:text/html;charset=utf-8');
    $info = json_decode(file_get_contents('php://input'), true);
    if (isset($info) && strlen($info['id']) === 16) {
        if ($info['hostname'] && $info['username'] && $info['password']) {
            extract($info);
            if ($redis->exists($id))
                $redis->del($id);
            $redis->hMSet($id, array('hostname' => $hostname, 'username' => $username, 'password' => $password));
        }
    }
} else if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    if (isset($_GET['id']) && $_GET['id']) {
        header('Access-Control-Allow-Origin:*');
        header("Content-type:application/json;charset=utf-8");
        if (strlen($_GET['id']) === 16 && $redis->exists($_GET['id'])) {
            echo json_encode($redis->hGetAll($_GET['id']));
            $redis->del($_GET['id']);
        }
    } else {
        header('Content-type:text/html;charset=utf-8');
        include('example.php');
    }
}

$redis->close();
