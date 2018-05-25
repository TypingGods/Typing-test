var TypingTest = {
    initialTime: 120,
    timeLeft: 0, //in seconds
    currentSpeed: 0,
    testStarted: false,
    currentLetter: 0,
    correctLetters: 0,
    wrongLetters: 0,
    textLength: 0,
    testText: "",
    textId: 0,
    enteredTextInDOM: undefined,
    timeInDOM: undefined,
    speedInDOM: undefined,
    counter: undefined,
    checkPoints: undefined,
    typingPaceMap: undefined,
    currentCheckPoint: 1,
    lastCheckPoint: 0,//time when last checkPoint speed measuring started

    init: function () {
        var startButton = document.getElementById('start-button');
        startButton.onclick = function () {
            TypingTest.testStarted = true;
            TypingTest.timeLeft = TypingTest.initialTime;
            TypingTest.wrongLetters = 0;
            TypingTest.currentSpeed = 0;
            TypingTest.currentLetter = 0;
            TypingTest.enteredTextInDOM.innerHTML = "";
            window.addEventListener("keydown", TypingTest.readLetter);
            window.addEventListener("keypress", TypingTest.readLetter);
            if(typeof TypingTest.counter !== undefined)
                clearInterval(TypingTest.counter);
            TypingTest.counter = setInterval(function () {
                if (TypingTest.timeLeft > 0) {
                    TypingTest.timeLeft -= 1;
                    TypingTest.timeInDOM.innerHTML = TypingTest.timeLeft;
                    TypingTest.speedInDOM.innerHTML = TypingTest.typingSpeedCPM();
                } else {
                    TypingTest.testStarted = false;
                }
            }, 1000);
            startButton.blur();
            document.getElementById('start-button').disabled = true;
        };
        var resetButton = document.getElementById('reset-button');
        resetButton.onclick = function () {
            window.location.reload(true);
        };
        var testText = document.getElementById('test-text');
        TypingTest.testText = testText.innerHTML;
        var textId = document.getElementById('text-id');
        TypingTest.textId = textId.innerHTML;
        this.timeInDOM = document.getElementById('time');
        this.speedInDOM = document.getElementById('speed');
        this.enteredTextInDOM = document.getElementById('entered-text');
        TypingTest.makeCheckPoints();
        TypingTest.typingPaceMap = new Map();
    },
    readLetter: function (e) {
        if (TypingTest.testStarted) {
            var enteredLetter = e.key;
            //console.log(enteredLetter);
            //console.log(e.keyCode);
            if (e.type === "keypress" && e.keyCode > 31 && e.keyCode < 127) {
                console.log('keypress');
                console.log(e.keyCode);
                var newNode = document.createElement("span");
                newNode.style["white-space"] = "pre-wrap";
                newNode.innerHTML = enteredLetter;
                if (enteredLetter === TypingTest.testText.charAt(TypingTest.currentLetter)) {
                    newNode.style["background-color"] = "green";
                    TypingTest.correctLetters += 1;
                } else {
                    newNode.style["background-color"] = "red";
                    TypingTest.wrongLetters += 1;
                }
                console.log(newNode);
                TypingTest.enteredTextInDOM.appendChild(newNode);
                TypingTest.currentLetter += 1;
                if(TypingTest.currentLetter === TypingTest.testText.length) {
                    clearInterval(TypingTest.counter);
                    window.removeEventListener("keydown", TypingTest.readLetter, false);
                    window.removeEventListener("keypress", TypingTest.readLetter, false);
                    TypingTest.showModal();
                }
            } else if (e.type === 'keydown' && enteredLetter === "Backspace") {
                console.log('keydown');
                if (TypingTest.enteredTextInDOM.childElementCount > 0) {
                    TypingTest.currentLetter -= 1;
                    TypingTest.enteredTextInDOM.removeChild(TypingTest.enteredTextInDOM.lastChild);
                }
            }
            TypingTest.typingPace();
        }
    },
    typingSpeedCPM: function () {
        return (TypingTest.correctLetters/((TypingTest.initialTime - TypingTest.timeLeft)/60)).toFixed(2);
    },
    typingAccuracy: function () {
        if (TypingTest.currentLetter > 0) {
            return (TypingTest.currentLetter - TypingTest.wrongLetters)*100/(TypingTest.currentLetter);
        } else {
            return 100;
        }
    },
    makeCheckPoints: function() {
        var textLength = this.testText.length;
        var numOfCheckPoints = 5;

        var checkPoints = new Array();
        var checkPointLength = Math.floor(textLength/numOfCheckPoints);
        var divisionRest = textLength % numOfCheckPoints;

        for(var i = 0; i < numOfCheckPoints; i++){
            checkPoints.push(i * checkPointLength - 1);
        }
        //last checkPoint may be longer
        checkPoints.push((numOfCheckPoints) * checkPointLength - 1 + divisionRest);

        this.checkPoints = checkPoints;
        console.log(this.checkPoints);
    },
    typingPace: function() {
        console.log("currentLetter: " + this.currentLetter);
        if(this.currentLetter === this.checkPoints[this.currentCheckPoint]) {
            var temp = this.currentCheckPoint;
            var numOfLetters = this.checkPoints[temp] - this.checkPoints[temp-1];
            var time = this.initialTime - this.timeLeft;
            var currentSpeed = this.currentTypingSpeed(numOfLetters, this.lastCheckPoint, time);

            console.log("pace: " + currentSpeed);

            this.typingPaceMap.set(this.checkPoints[temp], currentSpeed);
            this.lastCheckPoint = time;
            this.currentCheckPoint++;
        }
    },
    currentTypingSpeed: function(numOfLetters, startTime, endTime){
        return (numOfLetters/(endTime - startTime)*60).toFixed(2);
    },
    calculatePointsForSpeed: function (){
        var speedPoints;
        if (TypingTest.typingSpeedCPM() < 150) {
            speedPoints = 0;
        } else if (TypingTest.typingSpeedCPM() > 500){
            speedPoints = 70;
        } else {
            speedPoints = ((TypingTest.typingSpeedCPM()/5) - 30).toFixed();
        }
        return speedPoints;
    },
    calculatePointsForAccuracy: function () {
        var accuracyPoints;
        if (TypingTest.typingAccuracy() <= 90) {
            accuracyPoints = 0;
        } else {
            accuracyPoints = ((TypingTest.typingAccuracy() - 90).toFixed(0)) * 3;
        }
        return accuracyPoints;
    },
    showModal: function () {
        var modal = document.getElementById("nickname-modal");
        modal.style.display = "block";
    }

};
TypingTest.init();

function toggleVisibility(id){
    var element = document.getElementById(id);
    element.style.display = element.style.display === "block" ? "none" : "block";
}

function sendRequest(){
    var nickname = document.getElementById("nickname-input").value;
    toggleVisibility("nickname-modal");

    var url = document.URL + "/database";
    $.post(url,
        {
            userName : nickname,
            score: "10",
            textId: TypingTest.textId
        },
        function(data){
            $("html").html(data);
        });

}