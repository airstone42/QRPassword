const http = require('http')
const url = require('url')
const query = require('querystring')
const fs = require('fs')
const redis = require('redis')

const hostname = '192.168.0.2'
const port = 3000
const client = redis.createClient(6379, '127.0.0.1')

client.on('error', (err) => {
    throw err
});

const server = http.createServer((req, res) => {
    if (req.method === 'POST') {
        res.writeHead(200, {
            'Access-Control-Allow-Origin': '*',
            'Content-Type': 'text/html',
            'charset': 'utf-8'
        })
        let post = ''
        req.on('data', (data) => {
            post += data
        })
        req.on('end', () => {
            post = JSON.parse(post)
            // console.log(post)
            if (post.id && post.id.length === 8) {
                if (post.hostname && post.username && post.password) {
                    client.get(post.id, (err, value) => {
                        if (err) throw err;
                        if (value) client.del(post.id)
                        client.hmset(post.id, {
                            'hostname': post.hostname,
                            'username': post.username,
                            'password': post.password
                        })
                    })
                }
            }
        })
    } else if (req.method == 'GET') {
        if (url.parse(req.url, true).query.id) {
            res.writeHead(200, {
                'Access-Control-Allow-Origin': '*',
                'Content-Type': 'application/json',
                'charset': 'utf-8'
            })
            // console.log(url.parse(req.url, true).query.id)
            client.hgetall(url.parse(req.url, true).query.id, (err, value) => {
                if (err) throw err
                // console.log(value)
                res.end(JSON.stringify(value))
                client.del(url.parse(req.url, true).query.id)
            })
        } else {
            res.writeHead(200, {
                'Content-Type': 'text/html',
                'charset': 'utf-8'
            })
            fs.readFile('example.html', 'utf-8', (err, data) => {
                if (err) throw err
                res.end(data)
            })
        }
    }
})

server.listen(port, hostname, () => {
    console.log(`Server running at http://${hostname}:${port}/`)
})
