function parseMsg(msg) {
    return msg.split(";ttl=");
}

function getQueryParams() {
    const params = new URLSearchParams(window.location.search);
    const paramsObj = {};

    for (const [key, value] of params) {
        paramsObj[key] = value;
    }

    return paramsObj;
}

function sweetAlertWarning(msg) {
    const [_msg] = parseMsg(msg);

    Swal.fire({
        icon: 'warning',
        text: queryParams.msg,
    });
}