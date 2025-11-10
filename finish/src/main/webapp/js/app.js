async function loadQueries() {
    const response = await fetch("shipping/packageQuery")

    if (response.ok) {
        const list = await response.json();
        list.forEach(addToQueries)
    }
}

function addToQueries(item, index) {
    var container = document.createElement("div")
    container.id = "query" + index
    container.className = "hFlexContainer queryElement"


    var span = document.createElement("span")
    span.innerHTML = item.name
    container.appendChild(span)

    item.parameters.forEach((param, index) => {
        if (item.types[index] == "jakarta.data.Sort") {
            input = sortDropDown()
        } else {
            input = document.createElement("input")
            input.placeholder = param
            input.title = titleText(param)

        }
        input.setAttribute("jtype", item.types[index])
        container.appendChild(input)
    })

    var button = document.createElement("button")
    button.setAttribute("onclick", "callQuery(" + index + ")")
    button.alt = "Run the " + item.name + "query"
    button.innerHTML = "➜"

    container.appendChild(button)

    var node = document.getElementById("querySection")
    node.appendChild(container)

}

async function callQuery(index) {
    var node = document.getElementById("query" + index)

    var query = {}
    query.method = node.querySelector("span").innerHTML

    //Process Inputs
    var params = []
    var types = []
    console.log(node)

    console.log(node.children)
    console.log(Array.from(node.children))
    Array.from(node.children).forEach(input => {
        console.log(input.tagName)
        if (input.tagName == "INPUT") { //input
            params.push(input.value)
            types.push(input.getAttribute("jtype"))
        } else if (input.tagName == "DIV") { //sort
            var text = ""
            input.childNodes.forEach(select => {
                if (text == "")
                    text = select.options[select.selectedIndex].text
                else text = text + " " + select.options[select.selectedIndex].text
            })
            params.push(text)
            types.push(input.getAttribute("jtype"))
        }

    })


    query.parameters = params
    query.types = types
    console.log(query)

    //Return json object
    const response = await fetch("shipping/packageQuery", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(query),
    })

    processResponse(response)
}


async function processResponse(response) {
    if (response.ok) {
        const body = await response.text();
        console.log(body)
        if (body.length > 0) {
            var node = document.getElementById("resultsSection")
            node.style = "" //Make the results section visible

            try { //Return useful server exception to user
                var json = JSON.parse(body)
                console.log(json)
            } catch (error) {
                var div = document.createElement("div")
                div.innerHTML = body
                node.appendChild(div)
            }

            var table = document.getElementById("tableBody")
            var length = table.rows.length
            for (let i = 0; i < length; i++) table.deleteRow(0) //clear table

            console.log("removed table")
            for (m of json) {
                console.log("inserting row")
                var row = table.insertRow()
                row.insertCell().innerHTML = m.id;
                row.insertCell().innerHTML = m.length;
                row.insertCell().innerHTML = m.width;
                row.insertCell().innerHTML = m.height;
                row.insertCell().innerHTML = m.destination;
            }
        }
    } else {
        const message = await response.text();
        toast(message, 0)
    }

    console.log(response);
}

function sortDropDown(options) {
    var div = document.createElement("div")
    var params = document.createElement("select")

    var options = ["id", "length", "width", "height", "destination"]
    options.forEach(input => {
        var option = document.createElement("option")
        option.innerHTML = input
        params.appendChild(option)
    })

    var sort = document.createElement("select")
    var options = ["asc", "desc"]
    options.forEach(input => {
        var option = document.createElement("option")
        option.innerHTML = input
        sort.appendChild(option)
    })

    div.appendChild(params)
    div.appendChild(sort)
    return div
}

function titleText(param) {
    switch (param) {
        case "pageRequest":
            return "A pageRequest can be specified as a single number for a specific page (with page length of 10) or as a pair of numbers separated by a comma for page,pageSize e.g. 2,5"
        default:
            return ""
    }

}

function toast(message, index) {
    var length = 6000;
    var toast = document.getElementById("toast");
    setTimeout(function () { toast.innerText = message; toast.className = "show"; }, length * index);
    setTimeout(function () { toast.className = toast.className.replace("show", ""); }, length + length * index);
}