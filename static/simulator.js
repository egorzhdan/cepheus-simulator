function openField(input) {
    if (input.files === null || input.files.length === 0) {
        return;
    }

    var f = input.files[0];
    if (f.type !== 'text/plain') {
        alert(f.name + " is not a text file!");
        return;
    }
    var reader = new FileReader();
    reader.onload = (function (file) {
        return function (e) {
            var fieldData = e.target.result;
            loadField(fieldData)
        };
    })(f);
    reader.readAsText(f);
}

function loadField(fieldText) {
    var lines = fieldText.split('\n');
    for (var row = 0; row < 16; row++) {
        var cells = lines[row].split('_');
        for (var col = 0; col < 16; col++) {

            var td = $('#desc-' + row + '-' + col).parent();
            td.children('button').removeClass('btn-danger');

            // console.log(td.children('.btn-up'));
            var cell = cells[col];
            for (var idx = 0; idx < cell.length; idx++) {
                var wall = cell[idx];
                if (wall === 'U') {
                    td.children('.btn-up').trigger('click');
                }
                else if (wall === 'D') {
                    td.children('.btn-down').trigger('click');
                }
                else if (wall === 'L') {
                    td.children('.btn-left').trigger('click');
                }
                else if (wall === 'R') {
                    td.children('.btn-right').trigger('click');
                }
            }

        }
    }
}

var fieldDump = '';

function dumpField() {
    fieldDump = '';
    $('.field-tr').each(function (row, element) {
        firstInRow = true;
        $(element).children('.field-td').each(function (col, element) {
            var desc = 'c';
            $(element).children('.wall-selected').each(function (idx, button) {
                var btn = $(button);
                if (btn.hasClass('btn-up')) {
                    desc += 'U';
                }
                if (btn.hasClass('btn-down')) {
                    desc += 'D';
                }
                if (btn.hasClass('btn-left')) {
                    desc += 'L';
                }
                if (btn.hasClass('btn-right')) {
                    desc += 'R';
                }
            });

            if (!firstInRow) {
                fieldDump += '_';
            }
            firstInRow = false;
            fieldDump += desc;
        });
        fieldDump += '\n';
    });
}

function toggleCell(row, col, side, button) {
    $(button).toggleClass('btn-secondary');
    $(button).toggleClass('btn-danger');
    $(button).toggleClass('wall-selected');

    dumpField();

    var textFile = null;
    var makeTextFile = function (text) {
        var data = new Blob([text], {type: 'text/plain'});

        // If we are replacing a previously generated file we need to
        // manually revoke the object URL to avoid memory leaks.
        if (textFile !== null) {
            window.URL.revokeObjectURL(textFile);
        }

        textFile = window.URL.createObjectURL(data);

        // returns a URL you can use as a href
        return textFile;
    };

    var saveButton = document.getElementById('save');

    // Get rid of the event listeners:
    var elClone = saveButton.cloneNode(true);
    saveButton.parentNode.replaceChild(elClone, saveButton);
    saveButton = elClone;

    saveButton.addEventListener('click', function () {
        var link = document.createElement('a');
        link.setAttribute('download', 'field.txt');
        link.href = makeTextFile(fieldDump);
        document.body.appendChild(link);

        // wait for the link to be added to the document
        window.requestAnimationFrame(function () {
            var event = new MouseEvent('click');
            link.dispatchEvent(event);
            document.body.removeChild(link);
        });

    }, false);
}

var fileContents = '';

function readFile(event) {
    var input = event.target;

    var files = input.files;
    if (typeof files === 'undefined' || files.length < 1) {
        alert('Please provide a source file');
        runButton.show();
        return;
    }
    var file = files[0];

    var reader = new FileReader();
    reader.onload = function () {
        fileContents = reader.result;
    };
    reader.readAsText(file);
}

var robotRow = 0;
var robotCol = 0;

function resetField() {
    var spans = $('.desc-span').parent();
    spans.removeClass('table-secondary');
    spans.removeClass('table-danger');

    putRobot(0, 0);
}

function putRobot(row, col) {
    robotRow = row;
    robotCol = col;

    $('.desc-span').css('opacity', 0);

    var span = $('#desc-' + row + '-' + col);
    span.css('opacity', 1);
    span.parent().addClass('table-secondary');
}

function hitWall() {
    var span = $('#desc-' + robotRow + '-' + robotCol);
    span.parent().removeClass('table-secondary');
    span.parent().addClass('table-danger');
}

function run() {
    var logField = $('#log');

    logField.html('');
    var log = function (string, textClass) {
        var suffix = '';
        if (textClass) {
            suffix += '<p class="log-row text-' + textClass + '">';
        } else {
            suffix += '<p class="log-row">';
        }

        suffix += (string);

        suffix += '</p>';
        logField.append(suffix);
    };

    var runButton = $('#run');
    runButton.prop('disabled', true);

    var finished = false;

    resetField();

    log('Connecting...', 'muted');
    socket = new WebSocket("ws://" + window.location.host + "/simulator/ws");

    socket.onerror = function () {
        log('Socket error', 'danger');
    };

    socket.onopen = function () {
        log('Uploading...', 'muted');
        dumpField();
        socket.send(fieldDump);
        socket.send(fileContents);
        log('Uploaded', 'muted');
    };

    socket.onclose = function () {
        if (finished) {
            log('Disconnected', 'muted');
        } else {
            log('Disconnected unexpectedly', 'warning');
        }
        runButton.prop('disabled', false);
    };

    socket.onmessage = function (event) {
        var message = event.data.toString();

        if (message === '/finish') {
            log('Finished', 'success');
            finished = true;
        }
        else if (message.startsWith('/error')) {
            log('Error: ' + message.split('/error').join(''), 'danger');
        }
        else if (message.startsWith('/debug')) {
            log('> ' + message.split('/debug').join(''), 'info');
        }
        else if (message.startsWith('/robot')) {
            if (message.startsWith('/robot turn left')) {

            }
            else if (message.startsWith('/robot turn left')) {

            }
            else {
                var coordinates = message.split('{')[1].split('}')[0].split(', ');
                var row = parseInt(coordinates[0]);
                var col = parseInt(coordinates[1]);
                putRobot(row, col);
            }
        }
        else if (message.startsWith('/fail')) {
            log('Wall hit', 'danger');
            hitWall();
        }
        else {
            log(message);
        }
    };
}

resetField(0, 0);
