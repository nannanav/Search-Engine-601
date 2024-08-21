window.onload = function() {
    const jsonResultElement = document.getElementById("json-result");

    if (jsonResultElement && jsonResultElement.innerHTML.trim()) {
        try {
            const jsonObj = JSON.parse(jsonResultElement.innerHTML);

            const prettyJson = JSON.stringify(jsonObj, null, 4);
            jsonResultElement.textContent = prettyJson;
        } catch (e) {
            console.error("Invalid JSON: ", e);
        }
    }
};