function removeChildren(id) {
    var element = document.getElementById(id);
    while(element.firstChild){
        element.removeChild(element.firstChild);
    }
}

function setTestToStart() {
    removeChildren("entered-text");
    TypingTest.currentLetter = 0;
    TypingTest.correctLetters = 0;
    TypingTest.wrongLetters = 0;
    TypingTest.initialTime = 120;
    TypingTest.timeLeft = 0;

    TypingTest.currentCheckPoint = 1;
    TypingTest.lastCheckPoint = 0;
}

function setTestToStop() {
    TypingTest.initialTime = 120;
    TypingTest.timeLeft = 0;
    TypingTest.currentSpeed = 0;
    TypingTest.testStarted = false;
    TypingTest.currentLetter = 0;
    TypingTest.correctLetters = 0;
    TypingTest.wrongLetters = 0;
    TypingTest.textLength = 0;
    TypingTest.testText = "";
    TypingTest.textId = 0;
    TypingTest.enteredTextInDOM = undefined;
    TypingTest.timeInDOM = undefined;
    TypingTest.speedInDOM = undefined;
    TypingTest.counter = undefined;
    TypingTest.checkPoints = undefined;
    TypingTest.typingPaceMap = undefined;
    TypingTest.currentCheckPoint = 1;
    TypingTest.lastCheckPoint = 0;
}

/*
simulate typing text with given time breaks (in miliseconds).
jasmine.clock should be installed before calling (jasmine.clock.install()).
*/
function typeWithBreaks(str, time){
    for(var i = 0; i < str.length; i++){
        var charCode = str.charCodeAt(i);
        var character = str.charAt(i);
        var e = jQuery.Event("keypress", {keyCode: charCode, key: character});

        jasmine.clock().tick(time);
        TypingTest.readLetter(e);
    }
}

