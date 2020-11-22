import { Calendar, Alert } from 'antd';
import React from 'react'
import ReactDOM from 'react-dom'
import trLocale from 'moment/locale/tr';
import moment from 'moment';

moment.updateLocale('tr', trLocale);

class Calendarr extends React.Component {
    state = {
        value: moment('2017-01-25'),
        selectedValue: moment('2017-01-25'),
    };

    onSelect = value => {
        this.setState({
            value,
            selectedValue: value,
        });
    };

    onPanelChange = value => {
        this.setState({ value });
    };

    render() {
        const { value, selectedValue } = this.state;
        return (
            <>
                <Alert
                    message={`You selected date: ${selectedValue && selectedValue.format('YYYY-MM-DD')}`}
                />
                <Calendar value={value} onSelect={this.onSelect} onPanelChange={this.onPanelChange} />
            </>
        );
    }
}

export default Calendarr;