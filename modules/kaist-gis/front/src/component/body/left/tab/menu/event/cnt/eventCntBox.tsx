const EventCntBox = (props:{eventNm :string, cnt : number}) => {

    return(
        <li className="event__item">
            <p className="name gas">{props.eventNm}</p>
            <p className="value">{props.cnt}</p>
        </li>
    )

}

export default EventCntBox