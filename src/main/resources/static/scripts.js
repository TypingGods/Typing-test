var TypingTest = {
    initialTime: 120,
    timeLeft: 0, //in seconds
    currentSpeed: 0,
    testStarted: false,
    currentLetter: 0,
    wrongLetters: 0,
    textLength: 0,
    testText: "Życie jest jak pudełko czekoladek. Nigdy nie wiesz, co się trafi.",
    enteredTextInDOM: undefined,
    timeInDOM: undefined,
    speedInDOM: undefined,
    accuracyInDOM: undefined,
    counter: undefined,
    init: function () {
        var startButton = document.getElementById('start-button');
        startButton.onclick = function () {
            TypingTest.testStarted = true;
            TypingTest.timeLeft = TypingTest.initialTime;
            TypingTest.wrongLetters = 0;
            TypingTest.currentSpeed = 0;
            TypingTest.currentLetter = 0;
            TypingTest.enteredTextInDOM.innerHTML = "";
            this.counter = window.setInterval(function () {
                if (TypingTest.timeLeft > 0) {
                    TypingTest.timeLeft -= 1;
                    TypingTest.timeInDOM.innerHTML = TypingTest.timeLeft;
                    TypingTest.speedInDOM.innerHTML = TypingTest.typingSpeedCPM();
                    TypingTest.accuracyInDOM.innerHTML = TypingTest.typingAccuracy().toFixed(0) + '%';
                } else {
                    TypingTest.testStarted = false;
                }
            }, 1000);
        };
        window.addEventListener("keydown", this.readLetter);
        document.getElementById('reset-button');
        var testText = document.getElementById('test-text');
        testText.innerHTML = TypingTest.testText;
        this.timeInDOM = document.getElementById('time');
        this.speedInDOM = document.getElementById('speed');
        this.accuracyInDOM = document.getElementById('accuracy');
        this.enteredTextInDOM = document.getElementById('entered-text');
    },
    readLetter: function (e) {
        if (TypingTest.testStarted) {
            var enteredLetter = e.key;
            console.log(enteredLetter);
            if (e.keyCode > 31 && e.keyCode < 127) {
                var newNode = document.createElement("span");
                newNode.innerHTML = enteredLetter;
                if (enteredLetter === TypingTest.testText.charAt(TypingTest.currentLetter)) {
                    newNode.style["background-color"] = "green";
                } else {
                    newNode.style["background-color"] = "red";
                    TypingTest.wrongLetters += 1;
                }
                TypingTest.enteredTextInDOM.appendChild(newNode);
                TypingTest.currentLetter += 1;
            } else if (enteredLetter === "Backspace") {
                if (TypingTest.enteredTextInDOM.childElementCount > 0) {
                    TypingTest.currentLetter -= 1;
                    TypingTest.enteredTextInDOM.removeChild(TypingTest.enteredTextInDOM.lastChild);
                }
            }
        }
    },
    typingSpeedCPM: function () {
        return (TypingTest.currentLetter/((TypingTest.initialTime - TypingTest.timeLeft)/60)).toFixed(2);
    },
    typingAccuracy: function () {
        if (TypingTest.currentLetter > 0) {
            return (TypingTest.currentLetter - TypingTest.wrongLetters)*100/(TypingTest.currentLetter);
        } else {
            return 100;
        }
    }

};
TypingTest.init();