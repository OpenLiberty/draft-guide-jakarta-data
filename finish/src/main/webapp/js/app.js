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

    var button = document.createElement("button")
    button.setAttribute("onclick", "callQuery(" + index + ")")
    button.innerHTML = item.name

    container.appendChild(button)

    item.parameters.forEach((param, index) => {
        if (item.types[index] == "jakarta.data.Sort") {
            input = sortDropDown()
        } else {
            input = document.createElement("input")
            input.placeholder = param
        }
        input.setAttribute("jtype", item.types[index])
        container.appendChild(input)
    })

    var node = document.getElementById("querySection")
    node.appendChild(container)

}

async function callQuery(index) {
    var node = document.getElementById("query" + index)

    var query = {}
    query.method = node.getElementsByTagName("button")[0].innerHTML
    var inputs = node.getElementsByTagName("div")[0]

    //Process Inputs
    var params = []
    var types = []
    console.log(inputs.children)
    console.log(Array.from(inputs.children))
    Array.from(inputs.children).forEach(input => {
        console.log(input.tagName)
        if (input.tagName == "INPUT") { //input
            params.push(input.value)
        } else if (input.tagName == "DIV") { //sort
            var text = ""
            input.childNodes.forEach(select => {
                if (text == "")
                    text = select.options[select.selectedIndex].text
                else text = text + " " + select.options[select.selectedIndex].text
            })
            params.push(text)
        }
        types.push(input.getAttribute("jtype"))
    })

    query.parameters = params
    query.types = types

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
            node.replaceChildren() //clear the results section

            try { //Return useful server exception to user
                var json = JSON.parse(body)
            } catch (error) {
                var div = document.createElement("div")
                div.innerHTML = body
                node.appendChild(div)
            }

            for (m of json) {
                var div = document.createElement("div")
                div.innerHTML = "id = " + m.id;
                div.innerHTML += " length = " + m.length;
                div.innerHTML += " width = " + m.width;
                div.innerHTML += " height = " + m.height;
                div.innerHTML += " destination = " + m.destination;
                node.appendChild(div)
            }
        }
    } else {
        toast("Error! TODO better message", 0)
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

function toast(message, index) {
    var length = 3000;
    var toast = document.getElementById("toast");
    setTimeout(function () { toast.innerText = message; toast.className = "show"; }, length * index);
    setTimeout(function () { toast.className = toast.className.replace("show", ""); }, length + length * index);
}